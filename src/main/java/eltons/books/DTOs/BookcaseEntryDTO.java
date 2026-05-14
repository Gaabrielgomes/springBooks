package eltons.books.DTOs;

import java.time.LocalDate;

public record BookcaseEntryDTO(
        Long id,
        BookDTO book,
        String readingStatus,
        String review,
        LocalDate addedAt
) {}