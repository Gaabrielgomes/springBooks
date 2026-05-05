package eltons.books.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "author")
    private List<Book> books;

    public Author(){};

    public Author(String name) { this.name = name; }

}
