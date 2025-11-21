package ui;

import ui.command.Command;
// Імпортуйте ваш новий клас
import ui.command.MenuSeparatorCommand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {

    private final Map<String, Command> menuItems;
    private final Scanner scanner;

    public ConsoleMenu() {
        this.menuItems = new LinkedHashMap<>();
        this.scanner = new Scanner(System.in, "UTF-8");
    }

    public void addMenuItem(String key, Command command) {
        menuItems.put(key, command);
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("\nВведіть ваш вибір: ");
            String key = scanner.nextLine();

            Command command = menuItems.get(key);

            if (command != null) {
                if (command instanceof MenuSeparatorCommand) {
                    System.out.println("! Це лише заголовок. Оберіть номер команди.");
                    continue;
                }

                command.execute();

                if (command.getMenuTitle().equals("Вихід")) {
                    running = false;
                }
            } else {
                System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
        scanner.close();
        System.out.println("Програму завершено.");
    }

    private void printMenu() {
        System.out.println("\n\u001B[34mМеню програми:\u001B[0m");
        for (Map.Entry<String, Command> entry : menuItems.entrySet()) {

            Command command = entry.getValue();
            String key = entry.getKey();

            if (command instanceof MenuSeparatorCommand) {
                System.out.println(command.getMenuTitle());
            } else {
                System.out.println(key + ". " + command.getMenuTitle());
            }
        }
    }
}