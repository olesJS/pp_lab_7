package ui.command;

import service.ProductService;

public class AddNewDressingCommand implements Command {

    private final ProductService productService;

    public AddNewDressingCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.addNewDressing();
    }

    @Override
    public String getMenuTitle() {
        return "Додати нову заправку";
    }
}
