package ui.command;

import service.SaladService;

public class RemoveSaladCommand implements Command {

    private final SaladService saladService;

    public RemoveSaladCommand(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.removeSalad();
    }

    @Override
    public String getMenuTitle() {
        return "Видалити рецепт салату";
    }
}

