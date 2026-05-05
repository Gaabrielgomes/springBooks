package eltons.books.enums;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String genre;

    Gender(String g) {
        this.genre = g.toLowerCase();
    }

    public static Gender fromString(String text) {
        for (Gender c : Gender.values()) {
            if (c.genre.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Gender not exists: " + text);
    }
}
