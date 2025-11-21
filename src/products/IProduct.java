package products;

import java.util.Optional;

public interface IProduct {
    String getName();
    double getCaloriesPer100g();
    Optional<String> getCookingTip();
    String toTxtLine();
    String getUkrName();
}
