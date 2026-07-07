package eltons.books.DTOs;

public record BookSearchDTO(
        String intitle,
        String inauthor,
        String inpublisher,
        String subject,
        Long isbn
) {
    public BookSearchDTO(String intitle, String inauthor, String inpublisher, String subject, Long isbn) {
        this.intitle = intitle;
        this.inauthor = inauthor;
        this.inpublisher = inpublisher;
        this.subject = subject;
        if (isbn == null || isbn <= 0L) {
            this.isbn = 0L;
        } else {
            this.isbn = isbn;
        }
    }
}
