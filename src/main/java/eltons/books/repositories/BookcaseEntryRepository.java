package eltons.books.repositories;

import eltons.books.models.Book;
import eltons.books.models.BookcaseEntry;
import eltons.books.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookcaseEntryRepository extends JpaRepository<BookcaseEntry, Long> {
    boolean existsByUserAndBook(User user, Book book);
    List<BookcaseEntry> findAllByUser(User user);

    @Query(value = """
        SELECT *
        FROM bookcase_entry
        WHERE user_id = :userId
        AND book_id = :bookId
    """, nativeQuery=true)
    BookcaseEntry getByUserAndBook(Long userId, Long bookId);
}
