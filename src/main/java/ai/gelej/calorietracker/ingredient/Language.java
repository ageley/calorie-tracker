package ai.gelej.calorietracker.ingredient;

/**
 * Supported message languages, each carrying the labels used to render a confirmation message that
 * can be parsed back by the parser of the same language.
 *
 * @param savedComment the text of the comment line appended with a sparkles emoji
 * @param caloriesLabel the calories label
 * @param fatLabel the fat label
 * @param carbsLabel the carbs label
 * @param proteinLabel the protein label
 * @param energyUnit the canonical energy unit shown after the calories value
 * @param massUnit the canonical mass unit shown after each macronutrient value
 */
public enum Language {

    ENGLISH("Saved", "Calories", "Fat", "Carbs", "Protein", "kcal", "g"),
    RUSSIAN("Сохранено", "Калории", "Жир", "Углеводы", "Белки", "ккал", "г");

    private final String savedComment;
    private final String caloriesLabel;
    private final String fatLabel;
    private final String carbsLabel;
    private final String proteinLabel;
    private final String energyUnit;
    private final String massUnit;

    Language(String savedComment, String caloriesLabel, String fatLabel, String carbsLabel,
             String proteinLabel, String energyUnit, String massUnit) {
        this.savedComment = savedComment;
        this.caloriesLabel = caloriesLabel;
        this.fatLabel = fatLabel;
        this.carbsLabel = carbsLabel;
        this.proteinLabel = proteinLabel;
        this.energyUnit = energyUnit;
        this.massUnit = massUnit;
    }

    public String getSavedComment() {
        return savedComment;
    }

    public String getCaloriesLabel() {
        return caloriesLabel;
    }

    public String getFatLabel() {
        return fatLabel;
    }

    public String getCarbsLabel() {
        return carbsLabel;
    }

    public String getProteinLabel() {
        return proteinLabel;
    }

    public String getEnergyUnit() {
        return energyUnit;
    }

    public String getMassUnit() {
        return massUnit;
    }
}
