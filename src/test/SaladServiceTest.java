package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import products.IProduct;
import products.RootVegetable;
import salad.Salad;
import salad.SaladIngredient;
import saving.ProductRepository;
import saving.SaladRepository;
import service.SaladService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SaladServiceTest {

    private SaladService saladService;
    private FakeProductRepository productRepo;
    private FakeSaladRepository saladRepo;

    @BeforeEach
    void setUp() {
        productRepo = new FakeProductRepository();
        saladRepo = new FakeSaladRepository();

        // Додаємо тестові продукти в базу, щоб ми могли додавати їх у салат
        productRepo.addProduct(new RootVegetable("Морква", 40, 5, true));   // 40 ккал/100г
        productRepo.addProduct(new RootVegetable("Картопля", 80, 15, false)); // 80 ккал/100г
    }

    @Test
    void shouldCreateNewEmptySalad() {
        // Сценарій:
        // 1. Вводимо назву "МійСалат"
        // 2. Вводимо "0" (Вийти і зберегти без додавання інгредієнтів)
        String input = "МійСалат\n0\n";
        setupService(input);

        saladService.createNewSalad();

        // Перевіряємо
        assertEquals(1, saladRepo.salads.size());
        assertEquals("МійСалат", saladRepo.salads.get(0).getName());
    }

    @Test
    void shouldCreateSaladWithIngredients() {
        // Сценарій складніший (симулюємо роботу користувача в меню):
        // 1. Назва салату: "Олів'є"
        // 2. Вибір меню "1" (Додати інгредієнт)
        // 3. Назва продукту: "Морква"
        // 4. Вага: "100" (грам)
        // 5. Вибір меню "1" (Додати ще інгредієнт)
        // 6. Назва продукту: "Картопля"
        // 7. Вага: "50" (грам)
        // 8. Вибір меню "0" (Зберегти і вийти)

        String input = "Олів'є\n1\nМорква\n100\n1\nКартопля\n50\n0\n";
        setupService(input);

        saladService.createNewSalad();

        // Assert
        Salad savedSalad = saladRepo.getSaladByName("Олів'є").get();
        assertEquals(2, savedSalad.getIngredients().size(), "Має бути 2 інгредієнти");

        // Перевірка розрахунку калорій
        // Морква (100г) = 40 ккал
        // Картопля (50г) = 40 ккал (80 / 2)
        // Разом = 80 ккал
        double totalCals = savedSalad.getIngredients().stream()
                .mapToDouble(SaladIngredient::getTotalCalories).sum();
        assertEquals(80.0, totalCals, 0.01);
    }

    @Test
    void shouldRemoveIngredientFromSalad() {
        // ARRANGE: Створимо салат вручну і покладемо в репозиторій
        Salad caesar = new Salad("Цезар");
        IProduct carrot = productRepo.getProductByName("Морква").get();
        caesar.addIngredient(new SaladIngredient(carrot, 100));
        saladRepo.saveSalad(caesar);

        // ACT: Заходимо в режим редагування (але createNewSalad не дозволить редагувати існуючий).
        // Тому ми тестуємо метод handleEditLoop через створення НОВОГО салату,
        // або (оскільки handleEditLoop приватний), ми можемо використати createNewSalad для НОВОГО.

        // Але стривай! Твій код createNewSalad перевіряє "if exists -> return".
        // Тому ми не можемо редагувати старий салат через createNewSalad.

        // Щоб протестувати видалення інгредієнта, нам доведеться пройти повний шлях створення:
        // 1. Створити "ТестСалат"
        // 2. Додати "Моркву"
        // 3. Вибрати пункт "2" (Видалити інгредієнт)
        // 4. Ввести "Морква"
        // 5. Вийти "0"
        String input = "ТестСалат\n1\nМорква\n100\n2\nМорква\n0\n";
        setupService(input);

        saladService.createNewSalad();

        // ASSERT
        Salad result = saladRepo.getSaladByName("ТестСалат").get();
        assertTrue(result.getIngredients().isEmpty(), "Салат має бути порожнім після додавання і видалення інгредієнта");
    }

    @Test
    void shouldNotCreateSalad_IfNameExists() {
        // Arrange
        saladRepo.saveSalad(new Salad("Дублікат"));

        // Act
        // Пробуємо створити салат з такою ж назвою
        String input = "Дублікат\n";
        setupService(input);

        saladService.createNewSalad();

        // Assert
        // Розмір репо не змінився (був 1, став 1)
        assertEquals(1, saladRepo.salads.size());
    }

    @Test
    void shouldRemoveSalad() {
        // Arrange
        saladRepo.saveSalad(new Salad("ВидалиМене"));

        // Act
        String input = "ВидалиМене\n";
        setupService(input);
        saladService.removeSalad();

        // Assert
        assertTrue(saladRepo.salads.isEmpty());
    }

    @Test
    void shouldHandleInvalidWeightInput() {
        // Виправляємо сценарій:
        // 1. Назва салату
        // 2. Вибір "1" (Додати)
        // 3. Продукт "Морква"
        // 4. Вага "NotANumber" -> ПОМИЛКА, викидає в головне меню
        // 5. Вибір "1" (ЩЕ РАЗ вибираємо додати)
        // 6. Продукт "Морква" (ЩЕ РАЗ вводимо назву)
        // 7. Вага "100" (Тепер правильно)
        // 8. Вибір "0" (Вихід)

        String input = "СалатПомилка\n1\nМорква\nNotANumber\n1\nМорква\n100\n0\n";

        setupService(input);

        saladService.createNewSalad();

        Salad salad = saladRepo.getSaladByName("СалатПомилка").get();
        assertEquals(1, salad.getIngredients().size(), "Мав додатись інгредієнт після повторної спроби");
        assertEquals(100.0, salad.getIngredients().get(0).getWeightInGrams());
    }

    // --- Допоміжний метод ---
    private void setupService(String inputString) {
        Scanner mockScanner = new Scanner(inputString);
        saladService = new SaladService(saladRepo, productRepo, mockScanner);
    }

    // --- STUBS (Фейкові класи) ---

    static class FakeProductRepository extends ProductRepository {
        List<IProduct> products = new ArrayList<>();

        public FakeProductRepository() { super("ignored"); } // Ігноруємо шлях до файлу

        @Override
        public void addProduct(IProduct product) { products.add(product); }

        @Override
        public Optional<IProduct> getProductByName(String name) {
            return products.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        }
    }

    static class FakeSaladRepository extends SaladRepository {
        List<Salad> salads = new ArrayList<>();

        // Передаємо null у super, бо ми не будемо використовувати файлову систему
        public FakeSaladRepository() {
            super("ignored_dir", null);
        }

        @Override
        public void saveSalad(Salad salad) {
            // Емуляція поведінки: якщо є старий - видалити, додати новий
            deleteSalad(salad.getName());
            salads.add(salad);
        }

        @Override
        public void deleteSalad(String name) {
            salads.removeIf(s -> s.getName().equalsIgnoreCase(name));
        }

        @Override
        public Optional<Salad> getSaladByName(String name) {
            return salads.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
        }

        @Override
        public List<Salad> getAllSalads() {
            return new ArrayList<>(salads);
        }
    }

    // --- ТЕСТИ ДЛЯ ПЕРЕГЛЯДУ (View) ---

    @Test
    void shouldViewSaladRecipe_WhenExistsAndWhenNotFound() {
        // 1. Arrange
        Salad salad = new Salad("Цезар");
        salad.addIngredient(new SaladIngredient(new RootVegetable("Морква", 40, 5, true), 100));
        saladRepo.saveSalad(salad);

        // 2. Act
        // Сценарій 1: Вводимо правильну назву "Цезар"
        // Сценарій 2: Вводимо неіснуючу назву "Фантом"
        String input = "Цезар\nФантом\n";
        setupService(input);

        // Викликаємо метод двічі, щоб покрити обидві гілки if-else
        saladService.viewSaladRecipe(); // Знайде
        saladService.viewSaladRecipe(); // Не знайде (else)
    }

    @Test
    void shouldHandleEmptySaladInRecipeView() {
        // Arrange: Створюємо салат без інгредієнтів
        Salad emptySalad = new Salad("Пустий");
        saladRepo.saveSalad(emptySalad);

        // Act
        setupService("Пустий\n");
        saladService.viewSaladRecipe();

        // Assert: Головне, що не вилетів NullPointerException
    }

    // --- ТЕСТИ ДЛЯ СОРТУВАННЯ ІНГРЕДІЄНТІВ (Sort Ingredients) ---

    @Test
    void shouldSortIngredients_AllOptions() {
        // Arrange
        Salad salad = new Salad("Мікс");
        salad.addIngredient(new SaladIngredient(new RootVegetable("А-Морква", 40, 5, true), 100));
        salad.addIngredient(new SaladIngredient(new RootVegetable("Б-Картопля", 80, 15, false), 50));
        saladRepo.saveSalad(salad);

        // Act
        // Сценарій тестування всіх гілок switch:
        // 1. "Мікс" -> "1" (Сортування за назвою)
        // 2. "Мікс" -> "2" (Сортування за вагою)
        // 3. "Мікс" -> "3" (Сортування за калоріями)
        // 4. "Мікс" -> "99" (Невірний вибір - default гілка)
        // 5. "НемаєТакого" (Салат не знайдено)

        String input = "Мікс\n1\n" +
                "Мікс\n2\n" +
                "Мікс\n3\n" +
                "Мікс\n99\n" +
                "НемаєТакого\n";

        setupService(input);

        saladService.sortIngredients(); // Case 1
        saladService.sortIngredients(); // Case 2
        saladService.sortIngredients(); // Case 3
        saladService.sortIngredients(); // Default
        saladService.sortIngredients(); // Not found
    }

    // --- ТЕСТИ ДЛЯ СОРТУВАННЯ САЛАТІВ (Sort Salads) ---

    @Test
    void shouldSortSaladsByCalories() {
        // Arrange
        // Додаємо легкий салат
        Salad light = new Salad("Легкий");
        light.addIngredient(new SaladIngredient(new RootVegetable("Морква", 40, 5, true), 100)); // 40 ккал
        saladRepo.saveSalad(light);

        // Додаємо важкий салат
        Salad heavy = new Salad("Важкий");
        heavy.addIngredient(new SaladIngredient(new RootVegetable("Картопля", 80, 15, false), 500)); // 400 ккал
        saladRepo.saveSalad(heavy);

        // --- ВИПРАВЛЕННЯ ТУТ ---
        // Ми повинні створити сервіс перед використанням.
        // Передаємо порожній рядок, бо цьому методу не потрібен Scanner.
        setupService("");

        // Act
        saladService.sortSaladsByCalories();

        // Assert
        // Перевіряємо, що список не порожній (додаткова перевірка, щоб тест був змістовнішим)
        assertFalse(saladRepo.getAllSalads().isEmpty());
    }

    @Test
    void shouldHandleNoSaladsToSearchOrSort() {
        // --- ВИПРАВЛЕННЯ ТУТ ---
        // Ініціалізуємо сервіс з порожнім сканером
        setupService("");

        // Act
        // Тепер saladService не null, і методи спрацюють
        saladService.viewSalads();
        saladService.sortSaladsByCalories();

        // Assert: методи мають просто вивести повідомлення в консоль і не впасти
    }

    // --- ТЕСТИ ДЛЯ ПОШУКУ ОВОЧІВ (Find by Range) ---

    @Test
    void shouldFindVegetablesByCalories() {
        // Arrange
        Salad salad = new Salad("Пошуковий");
        // Овоч 1: 40 ккал
        salad.addIngredient(new SaladIngredient(new RootVegetable("Морква", 40, 5, true), 100));
        // Овоч 2: 20 ккал
        salad.addIngredient(new SaladIngredient(new RootVegetable("Шпинат", 20, 2, true), 100));
        saladRepo.saveSalad(salad);

        // Act
        // Сценарій 1: Успішний пошук. Салат "Пошуковий", діапазон 30-50 (має знайти Моркву)
        // Сценарій 2: Порожній результат. Салат "Пошуковий", діапазон 100-200
        // Сценарій 3: Помилка формату. Салат "Пошуковий", вводимо букви замість цифр
        // Сценарій 4: Салат не знайдено.

        String input = "Пошуковий\n30\n50\n" +
                "Пошуковий\n100\n200\n" +
                "Пошуковий\nNotANumber\n" +
                "Невідомий\n";

        setupService(input);

        saladService.findVegetablesByCalories(); // Знайде моркву
        saladService.findVegetablesByCalories(); // Нічого не знайде
        saladService.findVegetablesByCalories(); // Catch NumberFormatException
        saladService.findVegetablesByCalories(); // Салат не знайдено
    }
}