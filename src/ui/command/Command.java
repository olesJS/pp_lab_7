package ui.command;

// Визначає єдиний контракт для всіх конкретних команд (пунктів меню).
public interface Command {

    void execute();

    // Повертає текстову назву команди, яка буде відображатися в консольному меню.
    String getMenuTitle();
}