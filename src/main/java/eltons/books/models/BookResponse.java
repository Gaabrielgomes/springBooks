package eltons.books.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookResponse {
    private String kind;
    private int totalItems;
    private List<Item> items;

    public BookResponse() {}
}
