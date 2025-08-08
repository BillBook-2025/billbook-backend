package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class dataResponse {
    private BookListResponse data;
    private Pagenation pagenation;  //페이지네이션을 백에서 하는건지 아니면 프론트에서 하는건지
}
