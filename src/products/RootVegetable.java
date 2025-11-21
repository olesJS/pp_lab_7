package products;

import java.util.Optional;

// Коренеплоди (морква, буряк, пастернак, редис)
public class RootVegetable extends Vegetable {

    private double sugarContent; // кількість цукру на 100г
    private boolean isHard; // чи є твердим (потребує більше часу на приготування)

    public RootVegetable(String name, double caloriesPer100g, double sugarContent, boolean isHard) {
        super(name, caloriesPer100g);
        this.sugarContent = sugarContent;
        this.isHard = isHard;
    }

    public double getSugarContent() {
        return sugarContent;
    }

    public boolean isHard() {
        return isHard;
    }

    @Override
    public Optional<String> getCookingTip() {
        StringBuilder tips = new StringBuilder();

        if (this.isHard) {
            tips.append(this.getName() + " — твердий овоч. "
                    + "Для салату його краще нарізати тонкою соломкою або натерти. ");
        }

        if (this.sugarContent > 5.0) {
            tips.append(this.getName() + " має високий вміст цукру. "
                    + "Збалансуйте смак чимось кислим.");
        }

        if (tips.length() > 0) {
            return Optional.of(tips.toString().trim());
        }

        return Optional.empty();
    }

    @Override
    public String toTxtLine() {
        return String.format("RootVegetable;%s;%.2f;%.2f;%b",
                this.getName(), this.getCaloriesPer100g(), this.getSugarContent(), this.isHard());
    }

    @Override
    public String getUkrName() {
        return "Коренеплід";
    }
}
