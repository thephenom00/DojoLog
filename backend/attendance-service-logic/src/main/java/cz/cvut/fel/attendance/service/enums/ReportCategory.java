package cz.cvut.fel.attendance.service.enums;

public enum ReportCategory {
    TRAINING("Trénink"),
    TRAINING_CAMP("Soustředění"),
    COMPETITION("Závody"),
    EVENT("Akce"),
    SUBSTITUTION("Záskok"),

    TRAVEL_EXPENSE("Cestovné"),
    PART_TIME_WORK("Brigáda"),
    OTHER("Jiné");

    private final String name;

    ReportCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ReportCategory fromCzechName(String czechName) {
        if (czechName == null || czechName.isBlank()) {
            return null;
        }

        for (ReportCategory category : ReportCategory.values()) {
            if (category.getName().equalsIgnoreCase(czechName) || category.name().equalsIgnoreCase(czechName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category name: " + czechName);
    }
}
