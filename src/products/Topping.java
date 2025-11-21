package products;

import java.util.Optional;

// Додаток (сир, сухарики, насіння).
public class Topping implements IProduct {

    private final String name;
    private final double caloriesPer100g;

    // Чи є цей додаток хрустким (сухарики, горіхи).
    private final boolean isCrunchy;

    public Topping(String name, double caloriesPer100g, boolean isCrunchy) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.isCrunchy = isCrunchy;
    }

    public boolean isCrunchy() {
        return this.isCrunchy;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getCaloriesPer100g() {
        return this.caloriesPer100g;
    }

    @Override
    public Optional<String> getCookingTip() {
        if (this.isCrunchy) {
            String tip = "ПОРАДА: Хрусткий топінг '" + this.getName() + "' "
                    + "варто додавати в останню мить, щоб він не розмокнув.";
            return Optional.of(tip);
        }

        return Optional.empty();
    }

    @Override
    public String toTxtLine() {
        return String.format("Topping;%s;%.2f;%b",
                this.getName(), this.getCaloriesPer100g(), this.isCrunchy());
    }

    @Override
    public String getUkrName() {
        return "Топінг";
    }
}