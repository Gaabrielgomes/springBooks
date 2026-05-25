package eltons.books.DTOs;


import java.time.LocalDate;

public record BookDTO (
        Long id,
        Long isbn,
        String title,
        String author,
        String description,
        LocalDate publishedDate,
        Integer pagesNumber,
        String coverLink
) {
    public BookDTO(Long isbn, String title, String author, String description, LocalDate publishedDate, Integer pagesNumber, String coverLink) {
        this(0L, isbn, title, author, description, publishedDate, pagesNumber, coverLink);
    }
}