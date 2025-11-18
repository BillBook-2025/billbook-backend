package BillBook_2025_backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    private String address;
    private String latitude;
    private String longitude;
    private String regionLevel1;
    private String regionLevel2;
    private String regionLevel3;

}
