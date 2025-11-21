package salad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Salad {

    private String name;
    private final List<SaladIngredient> ingredients;

    public Salad(String name) {
        Objects.requireNonNull(name, "Назва салату не може бути null");
        this.name = name;
        this.ingredients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<SaladIngredient> getIngredients() {
        return new ArrayList<>(this.ingredients);
    }

    public void setName(String name) {
        Objects.requireNonNull(name, "Назва салату не може бути null");
        this.name = name;
    }

    public void addIngredient(SaladIngredient ingredient) {
        Objects.requireNonNull(ingredient, "Інгредієнт не може бути null");
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(SaladIngredient ingredient) {
        Objects.requireNonNull(ingredient, "Інгредієнт не може бути null");
        this.ingredients.remove(ingredient);
    }
}