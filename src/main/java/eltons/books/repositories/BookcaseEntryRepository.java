package eltons.books.repositories;

import eltons.books.models.Book;
import eltons.books.models.BookcaseEntry;
import eltons.books.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookcaseEntryRepository extends JpaRepository<BookcaseEntry, Long> {
    boolean existsByUserAndBook(User user, Book book);
    List<BookcaseEntry> findAllByUser(User user);

    BookcaseEntry getByUserAndBook(User user, Book book);
}
