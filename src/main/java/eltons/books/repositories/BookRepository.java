package eltons.books.repositories;

import eltons.books.DTOs.BookDTO;
import eltons.books.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<List<Book>> findAllByAuthorName(String authorName);

    Optional<Book> findByTitleIgnoreCaseAndAuthorNameIgnoreCase(String title, String author);

    Optional<Book> findByTitleIgnoreCase(String title);

    @NativeQuery(value = """
        SELECT
        b.isbn,
        b.title,
        authors.name,
        b.description,
        b.published_date,
        b.pages_number,
        b.cover_link
        FROM books AS b
        INNER JOIN authors ON authors.id = b.author_id
        ORDER BY b.id DESC
    """)
    List<BookDTO> getAllBooks();
}
