package ui.command;

import service.SaladService;

public class SortSaladsByCaloriesCommand implements Command {

    private final SaladService saladService;

    public SortSaladsByCaloriesCommand(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.sortSaladsByCalories();
    }

    @Override
    public String getMenuTitle() {
        return "Сортування салатів за калоріями";
    }
}

