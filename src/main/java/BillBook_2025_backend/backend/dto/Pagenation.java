package BillBook_2025_backend.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagenation {
    private int currentPage;
    private int totalPages;
    private int totalItems;

    public Pagenation() {}

    public Pagenation(int currentPage, int totalPages, int totalItems) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }
}
