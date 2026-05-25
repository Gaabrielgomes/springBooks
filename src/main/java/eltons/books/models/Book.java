package eltons.books.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "books")
public class Book {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(unique = true)
        private Long isbn;
        private String title;
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "author_id", nullable = false)
        private Author author;
        @Column(columnDefinition = "TEXT")
        private String description;
        private LocalDate publishedDate;
        private Integer pagesNumber;
        private String coverLink;

        @Builder
        public Book(Long isbn, String title, Author author, String description,
                    LocalDate publishedDate, Integer pagesNumber, String coverLink) {
                this.isbn = isbn;
                this.title = title;
                this.author = author;
                this.description = description;
                this.publishedDate = publishedDate;
                this.pagesNumber = pagesNumber;
                this.coverLink = coverLink;
        }

        @Override
        public String toString() {
                return "%s, from %s".formatted(getTitle(), getAuthor().getName());
        }
}
