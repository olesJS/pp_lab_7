package ui.command;

import service.ProductService;

public class AddNewToppingCommand implements Command {

    private final ProductService productService;

    public AddNewToppingCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.addNewTopping();
    }

    @Override
    public String getMenuTitle() {
        return "Додати новий топінг";
    }
}
