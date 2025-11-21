package products;

import java.util.Optional;

// Плодовий овоч (Помідор, огірок, болгарський перець, баклажан)
public class FruitingVegetable extends Vegetable {

    private double waterContentPercent;

    public FruitingVegetable(String name, double calories, double waterContentPercent) {
        super(name, calories);
        this.waterContentPercent = waterContentPercent;
    }

    public double getWaterContentPercent() {
        return waterContentPercent;
    }

    public boolean needsSeedRemoval() {
        return this.getName().equalsIgnoreCase("Болгарський перець");
    }

    @Override
    public Optional<String> getCookingTip() {
        StringBuilder tips = new StringBuilder();

        // 1. Порада на основі вмісту води (умовно > 90%)
        if (this.getWaterContentPercent() > 90.0) {
            tips.append(this.getName() + " містить багато води. "
                    + "Додавайте в останній момент, щоб салат не 'потік'. ");
        }

        // 2. Порада на основі насіння
        if (this.needsSeedRemoval()) {
            tips.append("Для " + this.getName() + " рекомендовано видалити насіння.");
        }

        if (tips.length() > 0) {
            return Optional.of(tips.toString().trim());
        }

        return Optional.empty();
    }

    @Override
    public String toTxtLine() {
        return String.format("FruitingVegetable;%s;%.2f;%.2f;%b",
                this.getName(), this.getCaloriesPer100g(), this.getWaterContentPercent(), this.needsSeedRemoval());
    }

    @Override
    public String getUkrName() {
        return "Плодовий";
    }

}