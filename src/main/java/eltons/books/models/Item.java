package eltons.books.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private String kind;
    private String id;
    private VolumeInfo volumeInfo;
}
