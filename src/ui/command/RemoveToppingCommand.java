package ui.command;

import service.ProductService;

public class RemoveToppingCommand implements Command {

    private final ProductService productService;

    public RemoveToppingCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.removeTopping();
    }

    @Override
    public String getMenuTitle() {
        return "Видалити топінг";
    }
}
