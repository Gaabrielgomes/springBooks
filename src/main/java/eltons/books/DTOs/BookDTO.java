package eltons.books.DTOs;


import java.time.LocalDate;

public record BookDTO (
        Long isbn,
        String title,
        String author,
        String description,
        LocalDate publishedDate,
        Integer pagesNumber,
        String coverLink
) {}