package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import products.*;
import salad.*;
import saving.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SaladRepositoryTest {

    // Шляхи до тимчасових даних
    private static final String TEST_PRODUCTS_FILE = "test_products_for_salad.txt";
    private static final String TEST_SALADS_DIR = "test_salads_dir";

    private ProductRepository productRepository;
    private SaladRepository saladRepository;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Очищаємо все старе перед тестом
        deleteDirectoryRecursively(Paths.get(TEST_SALADS_DIR));
        Files.deleteIfExists(Paths.get(TEST_PRODUCTS_FILE));

        // 2. Налаштовуємо ProductRepository і наповнюємо його даними
        productRepository = new ProductRepository(TEST_PRODUCTS_FILE);
        // Додаємо тестовий овоч, який будемо використовувати в салаті
        productRepository.addProduct(new RootVegetable("Морква", 41, 5, true));
        productRepository.addProduct(new RootVegetable("Картопля", 80, 15, false));

        // 3. Створюємо SaladRepository
        saladRepository = new SaladRepository(TEST_SALADS_DIR, productRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Прибираємо сміття після тесту
        deleteDirectoryRecursively(Paths.get(TEST_SALADS_DIR));
        Files.deleteIfExists(Paths.get(TEST_PRODUCTS_FILE));
    }

    @Test
    void shouldSaveSaladToFile() {
        // 1. Arrange
        Salad caesar = new Salad("Цезар");
        // Беремо продукт, який точно є в репозиторії
        IProduct carrot = productRepository.getProductByName("Морква").get();
        caesar.addIngredient(new SaladIngredient(carrot, 150.0));

        // 2. Act
        saladRepository.saveSalad(caesar);

        // 3. Assert
        // Перевіряємо, чи файл створився фізично
        Path expectedFile = Paths.get(TEST_SALADS_DIR, "Цезар.txt");
        assertTrue(Files.exists(expectedFile), "Файл салату має бути створений");

        // Перевіряємо, чи салат зберігся в пам'яті репозиторію
        Optional<Salad> saved = saladRepository.getSaladByName("Цезар");
        assertTrue(saved.isPresent());
    }

    @Test
    void shouldLoadSaladFromFile() {
        // Цей тест перевіряє повний цикл: Зберегти -> Очистити пам'ять -> Завантажити

        // 1. Arrange
        Salad originalSalad = new Salad("Олів'є");
        IProduct potato = productRepository.getProductByName("Картопля").get();
        originalSalad.addIngredient(new SaladIngredient(potato, 500.0)); // 500г картоплі

        saladRepository.saveSalad(originalSalad);

        // Створюємо НОВИЙ об'єкт репозиторію (імітуємо перезапуск програми)
        SaladRepository newSessionRepo = new SaladRepository(TEST_SALADS_DIR, productRepository);

        // 2. Act
        newSessionRepo.loadAllSalads();

        // 3. Assert
        Optional<Salad> loadedSaladOpt = newSessionRepo.getSaladByName("Олів'є");
        assertTrue(loadedSaladOpt.isPresent(), "Салат має завантажитись");

        Salad loadedSalad = loadedSaladOpt.get();
        assertEquals(1, loadedSalad.getIngredients().size(), "Має бути 1 інгредієнт");

        SaladIngredient ingredient = loadedSalad.getIngredients().get(0);
        assertEquals("Картопля", ingredient.getConsumable().getName());
        assertEquals(500.0, ingredient.getWeightInGrams());
    }

    @Test
    void shouldDeleteSaladFileAndFromMemory() throws IOException {
        // 1. Arrange
        Salad salad = new Salad("Грецький");
        saladRepository.saveSalad(salad);

        Path filePath = Paths.get(TEST_SALADS_DIR, "Грецький.txt");
        assertTrue(Files.exists(filePath)); // Переконалися, що файл є

        // 2. Act
        saladRepository.deleteSalad("Грецький");

        // 3. Assert
        assertFalse(Files.exists(filePath), "Файл має бути видалений");
        assertTrue(saladRepository.getSaladByName("Грецький").isEmpty(), "Салат має зникнути з пам'яті");
    }

    @Test
    void shouldSkipIngredient_WhenProductNotFoundInRepo() throws IOException {
        // ЦЕ ВАЖЛИВИЙ ТЕСТ
        // Ми створюємо файл салату вручну, в якому вказано продукт, якого НЕМАЄ в ProductRepository

        // 1. Arrange
        Path saladPath = Paths.get(TEST_SALADS_DIR, "ДивнийСалат.txt");
        // "Авокадо" немає в нашому productRepository (ми додали тільки Моркву і Картоплю)
        List<String> lines = List.of("Авокадо;200.0", "Морква;100.0");
        Files.write(saladPath, lines);

        // 2. Act
        saladRepository.loadAllSalads();

        // 3. Assert
        Optional<Salad> saladOpt = saladRepository.getSaladByName("ДивнийСалат");
        assertTrue(saladOpt.isPresent());

        Salad salad = saladOpt.get();
        assertEquals(1, salad.getIngredients().size(), "Мала завантажитись тільки Морква, бо Авокадо не знайдено");
        assertEquals("Морква", salad.getIngredients().get(0).getConsumable().getName());
    }

    // --- Допоміжний метод для видалення папки з усім вмістом ---
    // Стандартний Files.delete() падає, якщо папка не порожня
    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }

    @Test
    void shouldReturnSafeCopyOfSaladsList() {
        // 1. Arrange (Підготовка)
        // Додаємо два салати в репозиторій
        Salad caesar = new Salad("Цезар");
        Salad greek = new Salad("Грецький");

        saladRepository.saveSalad(caesar);
        saladRepository.saveSalad(greek);

        // 2. Act (Дія)
        List<Salad> retrievedList = saladRepository.getAllSalads();

        // 3. Assert (Перевірки)

        // Перевірка 1: Чи правильні дані повернулися?
        assertEquals(2, retrievedList.size(), "Має повернутися 2 салати");
        assertTrue(retrievedList.stream().anyMatch(s -> s.getName().equals("Цезар")));

        // Перевірка 2: (Найважливіша!) Чи це копія?
        // Ми видаляємо салат з ОТРИМАНОГО списку
        retrievedList.clear();

        // Але в РЕПОЗИТОРІЇ він має залишитись!
        assertEquals(0, retrievedList.size(), "Локальний список має стати порожнім");
        assertEquals(2, saladRepository.getAllSalads().size(), "Репозиторій не повинен постраждати від змін у отриманому списку");
    }
}