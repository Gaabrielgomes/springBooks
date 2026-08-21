package eltons.books.models.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public enum Gender {
    MALE("male"),
    FEMALE("female");

    @Enumerated(EnumType.STRING)
    private final String gender;

    Gender(String g) {
        this.gender = g.toLowerCase();
    }

    public static Gender fromString(String text) {
        for (Gender c : Gender.values()) {
            if (c.gender.equalsIgnoreCase(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Gender not exists: " + text);
    }
}
