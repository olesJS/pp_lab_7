package ui.command;

public class MenuSeparatorCommand implements Command {

    private final String title;

    public MenuSeparatorCommand(String title) {
        this.title = title;
    }

    @Override
    public void execute() {}

    @Override
    public String getMenuTitle() {
        return this.title;
    }
}