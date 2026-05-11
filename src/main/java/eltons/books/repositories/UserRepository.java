package eltons.books.repositories;

import eltons.books.DTOs.BookDTO;
import eltons.books.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);

    @Query(value = """
        SELECT
            books.title,
            authors.name,
            books.description,
            books.published_date,
            books.pages_number,
            books.cover_link
        FROM books
        INNER JOIN authors ON authors.id = books.author_id
        INNER JOIN bookcase_entry 
                ON bookcase_entry.book_id = books.id
        WHERE bookcase_entry.user_id = :userId
        ORDER BY bookcase_entry.added_at
    """, nativeQuery = true)
    List<BookDTO> getUserBookcase(Long userId);
}
