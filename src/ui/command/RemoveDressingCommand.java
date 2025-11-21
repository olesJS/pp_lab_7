package ui.command;

import service.ProductService;

public class RemoveDressingCommand implements Command {

    private final ProductService productService;

    public RemoveDressingCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.removeDressing();
    }

    @Override
    public String getMenuTitle() {
        return "Видалити заправку";
    }
}
