package eltons.books.DTOs;

public record BookSaveRequestDTO (
        String title,
        String author,
        String description,
        String publishedDate,
        Integer pagesNumber,
        String coverLink
){}
