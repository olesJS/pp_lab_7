package ui.command;

import service.ProductService;

public class ViewVegetablesCommand implements Command {

    private final ProductService productService;

    public ViewVegetablesCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.viewVegetables();
    }

    @Override
    public String getMenuTitle() {
        return "Подивитися овочі";
    }
}
