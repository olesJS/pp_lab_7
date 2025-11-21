package ui.command;

import service.ProductService;

public class ViewToppingsCommand implements Command {

    private final ProductService productService;

    public ViewToppingsCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.viewToppings();
    }

    @Override
    public String getMenuTitle() {
        return "Подивитися топінги";
    }
}
