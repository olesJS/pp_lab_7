package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import saving.ProductRepository;
import products.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    // Використовуємо окремий файл для тестів, щоб не чіпати основну базу
    private static final String TEST_FILE_PATH = "test_products_db.txt";
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        // Перед кожним тестом створюємо чистий репозиторій, що дивиться на тестовий файл
        repository = new ProductRepository(TEST_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Після кожного тесту видаляємо тестовий файл, щоб сміття не залишалось
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    @Test
    void shouldAddProductAndSaveToFile() {
        // 1. Arrange
        // Створюємо тестовий овоч (RootVegetable: назва, калорії, цукор, твердий)
        RootVegetable carrot = new RootVegetable("TestCarrot", 41.0, 5.0, true);

        // 2. Act
        repository.addProduct(carrot);

        // 3. Assert
        // Перевіряємо, чи є він у пам'яті
        List<IProduct> products = repository.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("TestCarrot", products.get(0).getName());

        // Перевіряємо, чи створився фізичний файл
        assertTrue(Files.exists(Paths.get(TEST_FILE_PATH)), "Файл має бути створений");
    }

    @Test
    void shouldLoadProductsFromFile() {
        // Цей тест перевіряє повний цикл: Зберегти -> Очистити пам'ять -> Завантажити з файлу

        // 1. Arrange
        RootVegetable v1 = new RootVegetable("Carrot", 41, 5, true);
        Dressing d1 = new Dressing("Mayo", 600, "Oil");

        repository.addProduct(v1);
        repository.addProduct(d1);

        // Створюємо НОВИЙ об'єкт репозиторію, щоб імітувати перезапуск програми
        ProductRepository newRepoSession = new ProductRepository(TEST_FILE_PATH);

        // 2. Act
        newRepoSession.loadFromFile();
        List<IProduct> loadedProducts = newRepoSession.getAllProducts();

        // 3. Assert
        assertEquals(2, loadedProducts.size(), "Мало завантажитись 2 продукти");

        // Перевіряємо типи завантажених об'єктів
        assertTrue(loadedProducts.stream().anyMatch(p -> p instanceof RootVegetable));
        assertTrue(loadedProducts.stream().anyMatch(p -> p instanceof Dressing));

        // Перевіряємо значення
        IProduct loadedCarrot = loadedProducts.stream().filter(p -> p.getName().equals("Carrot")).findFirst().get();
        assertEquals(41.0, loadedCarrot.getCaloriesPer100g());
    }

    @Test
    void shouldRemoveProductAndUpdateFile() {
        // 1. Arrange
        RootVegetable v1 = new RootVegetable("Onion", 30, 4, true);
        repository.addProduct(v1);

        // Переконуємось, що додали
        assertEquals(1, repository.getAllProducts().size());

        // 2. Act
        repository.removeProduct(v1);

        // 3. Assert
        // У пам'яті має бути пусто
        assertTrue(repository.getAllProducts().isEmpty());

        // У файлі теж має бути пусто (або файл порожній)
        ProductRepository checkRepo = new ProductRepository(TEST_FILE_PATH);
        checkRepo.loadFromFile();
        assertTrue(checkRepo.getAllProducts().isEmpty(), "Після видалення і перезавантаження список має бути порожнім");
    }

    @Test
    void shouldFindProductByNameIgnoreCase() {
        // Arrange
        repository.addProduct(new Topping("Croutons", 300, true));

        // Act & Assert
        Optional<IProduct> foundExact = repository.getProductByName("Croutons");
        assertTrue(foundExact.isPresent());

        Optional<IProduct> foundLower = repository.getProductByName("croutons");
        assertTrue(foundLower.isPresent(), "Пошук має ігнорувати регістр");

        Optional<IProduct> notFound = repository.getProductByName("Chips");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void shouldHandleCommaInCaloriesFromFile() throws IOException {
        // Цей тест перевіряє твій метод parseDoubleWithLocaleFix

        // 1. Arrange: Створюємо файл вручну з комою замість крапки
        // Формат: Type;Name;Calories;SpecificFields...
        String rawData = "LeafyVegetable;Spinach;23,5;2,1";
        Files.write(Paths.get(TEST_FILE_PATH), rawData.getBytes());

        // 2. Act: Пробуємо завантажити цей "неправильний" файл
        repository.loadFromFile();

        // 3. Assert
        List<IProduct> products = repository.getAllProducts();
        assertEquals(1, products.size());

        IProduct spinach = products.get(0);
        assertEquals(23.5, spinach.getCaloriesPer100g(), "Має коректно розпарсити 23,5 як 23.5");
        assertTrue(spinach instanceof LeafyVegetable);
    }

    @Test
    void shouldIgnoreCorruptedLines() throws IOException {
        // 1. Arrange: Записуємо сміття у файл
        String corruptedData = "Це просто текст без розділювачів\n" +
                "RootVegetable;BrokenData;NotANumber;5;true\n" + // Помилка числа
                "TuberVegetable;Potato;80;15"; // Коректний рядок

        Files.write(Paths.get(TEST_FILE_PATH), corruptedData.getBytes());

        // 2. Act
        repository.loadFromFile();

        // 3. Assert
        List<IProduct> products = repository.getAllProducts();
        assertEquals(1, products.size(), "Мав завантажитись лише 1 коректний продукт");
        assertEquals("Potato", products.get(0).getName());
    }

    @Test
    void shouldHandleSaveError_WhenWritingFails() throws IOException {
        // 1. Arrange (Підготовка)

        // Створюємо ПАПКУ з назвою, яку ми потім передамо як шлях до файлу
        String directoryName = "bad_path_is_a_directory";
        Path badPath = Paths.get(directoryName);
        Files.createDirectories(badPath); // Створили папку

        // Ініціалізуємо репозиторій.
        // Він буде думати, що "bad_path_is_a_directory" - це його файл для запису.
        ProductRepository repo = new ProductRepository(directoryName);

        // Додаємо продукт, щоб saveToFile() точно спробував щось записати
        repo.addProduct(new RootVegetable("Test", 10, 1, true));

        // Перехоплюємо System.err, щоб перевірити вивід помилки
        java.io.ByteArrayOutputStream errContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalErr = System.err;
        System.setErr(new java.io.PrintStream(errContent));

        try {
            // 2. Act (Дія)
            // Викликаємо метод збереження.
            // Files.write() спробує записати у папку і впаде з IOException.
            repo.saveToFile();

            // 3. Assert (Перевірка)
            String output = errContent.toString();

            // Перевіряємо, що спрацював catch і вивів повідомлення
            assertTrue(output.contains("Помилка збереження файлу продуктів"),
                    "Має бути виведено повідомлення про помилку збереження");

            // Можна також перевірити, що там є текст системної помилки (опціонально)
            // Наприклад, "Is a directory" або "Access is denied"

        } finally {
            System.setErr(originalErr);
            Files.deleteIfExists(badPath);
        }
    }

    @Test
    void shouldThrowException_WhenParsingNull_UsingReflection() throws Exception {
        // 1. Arrange
        ProductRepository repo = new ProductRepository("dummy.txt");

        java.lang.reflect.Method method = ProductRepository.class.getDeclaredMethod("parseDoubleWithLocaleFix", String.class);
        method.setAccessible(true);

        // 2. Act & Assert
        java.lang.reflect.InvocationTargetException exception = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                () -> method.invoke(repo, (String) null)
        );

        // 3. Перевіряємо справжню причину (Cause)
        assertTrue(exception.getCause() instanceof NumberFormatException);
        assertEquals("Рядок для парсингу є null.", exception.getCause().getMessage());
    }
}