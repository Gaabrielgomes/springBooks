package eltons.books.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeInfo {
    private String title;
    private List<String> authors;
    private String description;
    private String publishedDate;
    private Integer pageCount;
    private ImageLinks imageLinks;
}
