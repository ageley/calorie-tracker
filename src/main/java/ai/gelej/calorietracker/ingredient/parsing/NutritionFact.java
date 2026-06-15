package ai.gelej.calorietracker.ingredient.parsing;

import lombok.Getter;

/**
 * The four nutrition facts in their canonical Calories/Fat/Carbs/Protein order. Each fact knows
 * whether it is measured in energy or mass units, which constrains the units a parser will accept on
 * its line.
 */
@Getter
public enum NutritionFact {

    CALORIES(UnitKind.ENERGY),
    FAT(UnitKind.MASS),
    CARBS(UnitKind.MASS),
    PROTEIN(UnitKind.MASS);

    private final UnitKind unitKind;

    NutritionFact(UnitKind unitKind) {
        this.unitKind = unitKind;
    }

    /**
     * The kind of measure unit a nutrition fact is expressed in.
     */
    public enum UnitKind {
        ENERGY,
        MASS
    }
}
