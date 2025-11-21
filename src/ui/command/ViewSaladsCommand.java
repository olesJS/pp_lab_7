package ui.command;

import service.SaladService;

public class ViewSaladsCommand implements Command {

    private final SaladService saladService;

    public ViewSaladsCommand(SaladService saladService) {
        this.saladService = saladService;
    }

    @Override
    public void execute() {
        saladService.viewSalads();
    }

    @Override
    public String getMenuTitle() {
        return "Переглянути список салатів";
    }
}

