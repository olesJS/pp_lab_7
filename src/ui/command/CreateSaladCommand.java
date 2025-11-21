package ui.command;

import service.SaladService;

public class CreateSaladCommand implements Command {

    private final SaladService saladService;

    public CreateSaladCommand(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.createNewSalad();
    }

    @Override
    public String getMenuTitle() {
        return "Створити новий салат";
    }
}
