package eltons.books.models;

import eltons.books.models.enums.ReadingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "bookcase_entry")
public class BookcaseEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(columnDefinition = "TEXT")
    private String review;
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDate addedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "reading_status")
    private ReadingStatus readingStatus;

    @PrePersist
    private void prePersist() {
        this.addedAt = LocalDate.now();
    }
}