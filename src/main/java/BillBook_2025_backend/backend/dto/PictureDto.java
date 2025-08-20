package BillBook_2025_backend.backend.dto;

import lombok.Data;

@Data
public class PictureDto {
    private String filename;
    private String url;

    public PictureDto(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }
}
