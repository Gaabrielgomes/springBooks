package eltons.books.repositories;

import eltons.books.DTOs.BookDTO;
import eltons.books.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
        SELECT 
        b.title,
        authors.name,
        b.description,
        b.published_date,
        b.pages_number,
        b.cover_link
        FROM books AS b
        INNER JOIN authors ON authors.id = b.author_id
        WHERE books.title LIKE "%:title%"
        """, nativeQuery = true)
    List<BookDTO> findBookDTOByTitle(String title);

    Optional<Book> findByTitle(String title);

    Optional<Book> findByTitleIgnoreCase(String title);

    @Query(value = """
        SELECT
        b.title,
        authors.name,
        b.description,
        b.published_date,
        b.pages_number,
        b.cover_link
        FROM books AS b
        INNER JOIN authors ON authors.id = b.author_id
        """, nativeQuery = true)
    List<BookDTO> getAllBooks();
}
