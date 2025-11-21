package ui.command;

import service.ProductService;

public class RemoveVegetableCommand implements Command {

    private final ProductService productService;

    public RemoveVegetableCommand(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void execute() {
        productService.removeVegetable();
    }

    @Override
    public String getMenuTitle() {
        return "Видалити овоч";
    }
}
