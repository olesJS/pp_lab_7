package products;

import java.util.Optional;

// Бульбоплід (Картопля, топінамбур, батат)
public class TuberVegetable extends Vegetable {

    private double starchContent; // Кількість крохмалю на 100г

    public TuberVegetable(String name, double calories, double starchContent) {
        super(name, calories);
        this.starchContent = starchContent;
    }

    public double getStarchContent() {
        return starchContent;
    }

    @Override
    public Optional<String> getCookingTip() {
        String tip = this.getName() + " (" + this.starchContent + "г крохмалю) "
                + "потребує термічної обробки (варіння/запікання) перед додаванням у салат!";
        return Optional.of(tip);
    }

    @Override
    public String toTxtLine() {
        return String.format("TuberVegetable;%s;%.2f;%.2f",
                this.getName(), this.getCaloriesPer100g(), this.getStarchContent());
    }

    @Override
    public String getUkrName() {
        return "Бульбоплід";
    }

}