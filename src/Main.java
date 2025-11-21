import ui.ConsoleMenu;
import ui.command.*;
import service.*;
import saving.*;

public class Main {

    public static void main(String[] args) {

        ProductRepository prodRepo = new ProductRepository("product_repo");
        SaladRepository saladRepo = new SaladRepository("salad_repo", prodRepo);

        prodRepo.loadFromFile();
        saladRepo.loadAllSalads();

        // Отримувачі
        ProductService productService = new ProductService(prodRepo);
        SaladService saladService = new SaladService(saladRepo, prodRepo);

        // Викликач
        ConsoleMenu menu = new ConsoleMenu();

        // Products Commands
        Command addVegetable = new AddNewVegetableCommand(productService);
        Command addDressing = new AddNewDressingCommand(productService);
        Command addTopping = new AddNewToppingCommand(productService);

        Command viewVegetables = new ViewVegetablesCommand(productService);
        Command viewDressings = new ViewDressingsCommand(productService);
        Command viewToppings = new ViewToppingsCommand(productService);

        Command removeVegetable = new RemoveVegetableCommand(productService);
        Command removeDressing = new RemoveDressingCommand(productService);
        Command removeTopping = new RemoveToppingCommand(productService);

        // Salad Commands
        Command createSalad = new CreateSaladCommand(saladService);
        Command viewSalads = new ViewSaladsCommand(saladService);
        Command deleteSalad = new RemoveSaladCommand(saladService);

        Command viewSaladRecipe = new ViewSaladRecipe(saladService);
        Command sortSaladIngridients = new SortIngredientsCommand(saladService);
        Command sortSalads = new SortSaladsByCaloriesCommand(saladService);
        Command findVegetablesByCalories = new FindVegetablesByCaloriesCommand(saladService);

        Command exit = new ExitCommand();

        menu.addMenuItem("s1", new MenuSeparatorCommand("\u001B[32m--- Керування овочами ---\u001B[0m"));
        menu.addMenuItem("1", viewVegetables);
        menu.addMenuItem("2", addVegetable);
        menu.addMenuItem("3", removeVegetable);

        menu.addMenuItem("s2", new MenuSeparatorCommand("\u001B[32m--- Керування заправками ---\u001B[0m"));
        menu.addMenuItem("4", viewDressings);
        menu.addMenuItem("5", addDressing);
        menu.addMenuItem("6", removeDressing);

        menu.addMenuItem("s3", new MenuSeparatorCommand("\u001B[32m--- Керування топінгами ---\u001B[0m"));
        menu.addMenuItem("7", viewToppings);
        menu.addMenuItem("8", addTopping);
        menu.addMenuItem("9", removeTopping);

        menu.addMenuItem("s4", new MenuSeparatorCommand("\u001B[32m--- Керування салатами ---\u001B[0m"));
        menu.addMenuItem("10", createSalad);
        menu.addMenuItem("11", viewSalads);
        menu.addMenuItem("12", deleteSalad);

        menu.addMenuItem("13", viewSaladRecipe);
        menu.addMenuItem("14", sortSaladIngridients);
        menu.addMenuItem("15", sortSalads);
        menu.addMenuItem("16", findVegetablesByCalories);

        menu.addMenuItem("s5", new MenuSeparatorCommand("\u001B[31m--- Вихід ---\u001B[0m"));
        menu.addMenuItem("0", exit);

        menu.run();
    }
}