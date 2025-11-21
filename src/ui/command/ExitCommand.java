package ui.command;

public class ExitCommand implements Command {

    @Override
    public void execute() {
        System.out.println("Завершення роботи програми...");
    }

    @Override
    public String getMenuTitle() {
        return "Вихід";
    }
}