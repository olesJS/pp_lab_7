package ui.command;

import service.ProductService;

public class ViewDressingsCommand implements Command {

    private final ProductService productService;

    public ViewDressingsCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.viewDressings();
    }

    @Override
    public String getMenuTitle() {
        return "Подивитися заправки";
    }
}
