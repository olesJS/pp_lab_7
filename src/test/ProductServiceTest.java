package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import products.*;
import saving.ProductRepository;
import service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;
    private FakeProductRepository fakeRepository;

    @BeforeEach
    void setUp() {
        // Очищаємо репозиторій перед кожним тестом
        fakeRepository = new FakeProductRepository();
    }

    // --- ТЕСТИ ДОДАВАННЯ ОВОЧІВ ---

    @Test
    void shouldAddRootVegetable() {
        // Input: Назва -> Калорії -> Тип(1) -> Цукор -> Твердий
        String input = "Морква\n41\n1\n5.0\ntrue\n";
        setupServiceWithInput(input);

        productService.addNewVegetable();

        assertEquals(1, fakeRepository.products.size());
        IProduct product = fakeRepository.products.get(0);
        assertTrue(product instanceof RootVegetable);
        assertEquals("Морква", product.getName());
    }

    @Test
    void shouldAddLeafyVegetable() {
        // Input: Назва -> Калорії -> Тип(2) -> Клітковина
        String input = "Шпинат\n23\n2\n2.2\n";
        setupServiceWithInput(input);

        productService.addNewVegetable();

        IProduct product = fakeRepository.products.get(0);
        assertTrue(product instanceof LeafyVegetable);
        assertEquals("Шпинат", product.getName());
    }

    @Test
    void shouldAddFruitingVegetable() {
        // Input: Назва -> Калорії -> Тип(3) -> Вода
        String input = "Помідор\n18\n3\n95\n";
        setupServiceWithInput(input);

        productService.addNewVegetable();

        IProduct product = fakeRepository.products.get(0);
        assertTrue(product instanceof FruitingVegetable);
    }

    @Test
    void shouldAddTuberVegetable() {
        // Input: Назва -> Калорії -> Тип(4) -> Крохмаль
        String input = "Картопля\n77\n4\n15\n";
        setupServiceWithInput(input);

        productService.addNewVegetable();

        IProduct product = fakeRepository.products.get(0);
        assertTrue(product instanceof TuberVegetable);
    }

    @Test
    void shouldNotAddVegetable_WhenChoiceIsInvalid() {
        // Input: Назва -> Калорії -> Тип(99 - не існує)
        String input = "Щось\n10\n99\n";
        setupServiceWithInput(input);

        productService.addNewVegetable();

        assertTrue(fakeRepository.products.isEmpty(), "Не повинно додавати продукт при неправильному виборі меню");
    }

    @Test
    void shouldHandleNumberFormatException_InVegetables() {
        // Input: Назва -> "не число"
        String input = "Морква\nбагато\n";
        setupServiceWithInput(input);

        productService.addNewVegetable();

        assertTrue(fakeRepository.products.isEmpty(), "Програма не повинна падати, список має залишитись порожнім");
    }

    // --- ТЕСТИ ДОДАВАННЯ ІНШИХ ПРОДУКТІВ ---

    @Test
    void shouldAddDressing() {
        // Input: Назва -> Калорії -> Тип
        String input = "Майонез\n680\nОлійна\n";
        setupServiceWithInput(input);

        productService.addNewDressing();

        assertEquals(1, fakeRepository.products.size());
        assertTrue(fakeRepository.products.get(0) instanceof Dressing);
    }

    @Test
    void shouldAddTopping() {
        // Input: Назва -> Калорії -> Хрусткий(true)
        String input = "Сухарики\n300\ntrue\n";
        setupServiceWithInput(input);

        productService.addNewTopping();

        assertEquals(1, fakeRepository.products.size());
        assertTrue(fakeRepository.products.get(0) instanceof Topping);
    }

    // --- ТЕСТИ ВИДАЛЕННЯ ---

    @Test
    void shouldRemoveVegetable_WhenExists() {
        // Arrange
        Vegetable v = new RootVegetable("Буряк", 40, 6, true);
        fakeRepository.addProduct(v);

        // Act (Input: назва для видалення)
        setupServiceWithInput("Буряк\n");
        productService.removeVegetable();

        // Assert
        assertTrue(fakeRepository.products.isEmpty());
    }

    @Test
    void shouldNotRemoveVegetable_IfItIsDressing() {
        // Цей тест перевіряє, чи метод removeVegetable випадково не видалить заправку

        // Arrange
        Dressing d = new Dressing("Соус", 100, "Олія");
        fakeRepository.addProduct(d); // Додали заправку

        // Act: Пробуємо видалити "Соус" через меню ОВОЧІВ
        setupServiceWithInput("Соус\n");
        productService.removeVegetable();

        // Assert
        assertEquals(1, fakeRepository.products.size(), "Заправка не має видалятись через метод removeVegetable");
    }

    @Test
    void shouldRemoveTopping() {
        // Arrange
        Topping t = new Topping("Горіхи", 600, true);
        fakeRepository.addProduct(t);

        // Act
        setupServiceWithInput("Горіхи\n");
        productService.removeTopping();

        // Assert
        assertTrue(fakeRepository.products.isEmpty());
    }

    @Test
    void shouldRemoveDressing() {
        // Arrange
        Dressing d = new Dressing("Олія", 900, "Олійна");
        fakeRepository.addProduct(d);

        // Act
        setupServiceWithInput("Олія\n");
        productService.removeDressing();

        // Assert
        assertTrue(fakeRepository.products.isEmpty());
    }

    // --- Допоміжний метод ---
    private void setupServiceWithInput(String inputData) {
        Scanner mockScanner = new Scanner(inputData);
        productService = new ProductService(fakeRepository, mockScanner);
    }

    // --- Фейковий репозиторій (Stub) ---
    // Це замінює реальний файл, щоб тести працювали швидко і в пам'яті
    static class FakeProductRepository extends ProductRepository {

        List<IProduct> products = new ArrayList<>();

        // Важливо: передаємо null або щось пусте у super, бо ми не будемо використовувати файл
        public FakeProductRepository() {
            super("ignored_path.txt");
        }

        @Override
        public void addProduct(IProduct product) {
            products.add(product);
        }

        @Override
        public void removeProduct(IProduct product) {
            products.remove(product);
        }

        @Override
        public List<IProduct> getAllProducts() {
            return new ArrayList<>(products);
        }

        @Override
        public Optional<IProduct> getProductByName(String name) {
            return products.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .findFirst();
        }

        // Перевизначаємо методи роботи з файлами, щоб вони нічого не робили
        @Override
        public void saveToFile() { }

        @Override
        public void loadFromFile() {  }
    }
}