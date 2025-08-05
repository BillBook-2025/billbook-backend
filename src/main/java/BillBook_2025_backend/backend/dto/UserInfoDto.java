package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDto {

    private String userId;
    private String password;
    private String email;
    private double temperature;
    private String userName;

    public UserInfoDto(Member member) {
        this.userId = member.getUserId();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.temperature = member.getTemperature();
        this.userName = member.getUserName();
    }
}
