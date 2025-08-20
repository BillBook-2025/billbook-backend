package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PictureDtoList {
    private List<PictureDto> pictures;

    public PictureDtoList(List<PictureDto> dto) {
        this.pictures = dto;
    }
}
