package ui.command;

import service.SaladService;

public class SortIngredientsCommand implements Command {

    private final SaladService saladService;

    public SortIngredientsCommand(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.sortIngredients();
    }

    @Override
    public String getMenuTitle() {
        return "Сортування інгредієнтів салату за певним критерієм";
    }
}

