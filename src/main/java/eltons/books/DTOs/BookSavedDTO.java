package eltons.books.DTOs;

public record BookSavedDTO(
        Long id,
        String title,
        String author,
        String coverLink
) {}