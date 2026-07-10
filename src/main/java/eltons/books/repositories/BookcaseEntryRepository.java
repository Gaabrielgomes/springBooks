package eltons.books.repositories;

import eltons.books.models.Book;
import eltons.books.models.BookcaseEntry;
import eltons.books.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;

public interface BookcaseEntryRepository extends JpaRepository<BookcaseEntry, Long> {
    boolean existsByUserAndBook(User user, Book book);
    List<BookcaseEntry> findAllByUser(User user);

    @NativeQuery(value = """
        SELECT *
        FROM bookcase_entry
        WHERE user_id = :userId
        AND book_id = :bookId
    """)
    BookcaseEntry getByUserAndBook(Long userId, Long bookId);
}
