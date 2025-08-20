package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PictureUrl {
    private List<String> urls;

    public PictureUrl(List<String> urls) {
        this.urls = urls;
    }
}
