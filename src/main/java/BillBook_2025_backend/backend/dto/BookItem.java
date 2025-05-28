package BillBook_2025_backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class BookItem {
    private String title;
    private String author;
    private String publisher;
    private String category = null;
    private String isbn;
    private String description;


    BookItem(@JsonProperty("title") String title,
             @JsonProperty("author") String author,
             @JsonProperty("publisher") String publisher,
             @JsonProperty("isbn") String isbn,
             @JsonProperty("description") String description) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.description = description;
    }
}
