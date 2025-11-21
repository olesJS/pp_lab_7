package products;

import java.util.Optional;

// Листовий овоч (Салат, шпинат, капуста, рукола)
public class LeafyVegetable extends Vegetable {

    private double fiberContent; // вміст клітковини на 100г

    public LeafyVegetable(String name, double caloriesPer100g, double fiberContent) {
        super(name, caloriesPer100g);
        this.fiberContent = fiberContent;
    }

    public double getFiberContent() {
        return fiberContent;
    }

    @Override
    public Optional<String> getCookingTip() {
        StringBuilder tips = new StringBuilder();

        if (this.fiberContent > 3.0) {
            tips.append(this.getName() + " має досить жорстке листя (багато клітковини). "
                    + "Наріжте його дрібно або 'помасажуйте' з заправкою. ");
        }

        tips.append(this.getName() + " — ніжний продукт. Зберігайте в холодильнику, вжити протягом 1-3 днів.");

        return Optional.of(tips.toString().trim());
    }

    @Override
    public String toTxtLine() {
        return String.format("LeafyVegetable;%s;%.2f;%.2f",
                this.getName(), this.getCaloriesPer100g(), this.getFiberContent());
    }

    @Override
    public String getUkrName() {
        return "Листовий";
    }

}
