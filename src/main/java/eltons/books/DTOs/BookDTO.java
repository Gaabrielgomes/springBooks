package eltons.books.DTOs;


import java.time.LocalDate;

public record BookDTO (
        String title,
        String author,
        String description,
        LocalDate publishedDate,
        Integer pageCount,
        String thumbnail
) {}