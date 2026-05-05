package eltons.books.models;

import eltons.books.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate birth;
    private Gender gender;
    private String selfDescription;
    private String password;
    private String role;

    public User(String name, LocalDate birth, Gender gender, String selfDescription) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.selfDescription = selfDescription;
    }
}
