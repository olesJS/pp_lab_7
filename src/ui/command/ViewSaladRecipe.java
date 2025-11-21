package ui.command;

import service.SaladService;

public class ViewSaladRecipe implements Command {

    private final SaladService saladService;

    public ViewSaladRecipe(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.viewSaladRecipe();
    }

    @Override
    public String getMenuTitle() {
        return "Переглянути рецепт салату";
    }
}

