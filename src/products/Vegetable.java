package products;

import java.util.Optional;

public abstract class Vegetable implements IProduct {

    private String name;
    private double caloriesPer100g;

    public Vegetable(String name, double caloriesPer100g) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
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
        return Optional.empty();
    }

    @Override
    public String toTxtLine() {
        return "toTxtLine function";
    }
}
