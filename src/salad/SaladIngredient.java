package salad;

import products.IProduct;
import java.util.Objects;

public class SaladIngredient {

    private final IProduct product;
    private final double weightInGrams;

    public SaladIngredient(IProduct product, double weightInGrams) {
        Objects.requireNonNull(product, "Продукт (consumable) не може бути null");
        if (weightInGrams <= 0) {
            throw new IllegalArgumentException("Вага має бути додатним числом.");
        }

        this.product = product;
        this.weightInGrams = weightInGrams;
    }

    public IProduct getConsumable() {
        return product;
    }

    public double getWeightInGrams() {
        return weightInGrams;
    }

    public double getTotalCalories() {
        return (product.getCaloriesPer100g() / 100.0) * weightInGrams;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f г) - %.2f ккал",
                product.getName(),
                weightInGrams,
                getTotalCalories());
    }
}