package products;

import java.util.Optional;

// Заправка для салату (олія, соус, майонез).
public class Dressing implements IProduct {

    private final String name;
    private final double caloriesPer100g;

    // Тип основи заправки ("Олійна", "Вершкова", "Кисломолочна", "Кисла").
    private final String baseType;

    public Dressing(String name, double caloriesPer100g, String baseType) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.baseType = baseType;
    }

    public String getBaseType() {
        return this.baseType;
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
        StringBuilder tip = new StringBuilder();

        tip.append("Заправку '").append(this.getName()).append("' ");
        tip.append("краще додавати безпосередньо перед подачею, щоб салат не осів.");

        switch (this.baseType.toLowerCase()) {
            case "олійна":
                tip.append("Уникайте нагрівання — зберігайте в прохолодному, темному місці, щоб запобігти окисленню. ");
                tip.append("Не змішуйте з водними розчинами без емульгатора — можливе розшарування.");
                break;
            case "кисла":
                tip.append("Перед використанням добре збовтайте або перемішайте до стану емульсії.");
                break;
            case "вершкова":
                tip.append("Зберігайте в холодильнику. Не змішуйте з гарячими інгредієнтами, щоб уникнути розшарування.");
                break;
            default:
                break;
        }

        return Optional.of(tip.toString());
    }

    @Override
    public String toTxtLine() {
        return String.format("Dressing;%s;%.2f;%s",
                this.getName(), this.getCaloriesPer100g(), this.getBaseType());
    }

    @Override
    public String getUkrName() {
        return "Заправка";
    }
}