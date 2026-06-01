package eltons.books.models.enums;

public enum ReadingStatus {
    WANT_TO_READ("want_to_read"),
    READING("reading"),
    READ("read");

    private String readingStatus;

    ReadingStatus(String rs) { this.readingStatus = rs; }

    public static ReadingStatus fromString(String text) {
        for (ReadingStatus rs : ReadingStatus.values()) {
            if (rs.readingStatus.equalsIgnoreCase(text)) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Reading status does not exist: " + text);
    }
}
