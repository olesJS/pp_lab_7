package ui.command;

import service.ProductService;

public class AddNewVegetableCommand implements Command {

    private final ProductService productService;

    public AddNewVegetableCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.addNewVegetable();
    }

    @Override
    public String getMenuTitle() {
        return "Додати новий овоч";
    }
}
