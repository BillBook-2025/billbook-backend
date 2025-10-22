package BillBook_2025_backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    Long id;
    String username;
    String email;
    String profilePic;
    Double temperature;
}
