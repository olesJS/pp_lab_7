package ui.command;

import service.SaladService;

public class FindVegetablesByCaloriesCommand implements Command {

    private final SaladService saladService;

    public FindVegetablesByCaloriesCommand(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.findVegetablesByCalories();
    }

    @Override
    public String getMenuTitle() {
        return "Знайти овочі за заданим діапазоном калорій";
    }
}

