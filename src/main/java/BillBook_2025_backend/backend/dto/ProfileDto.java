package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDto {
    private String username;
    private Double temperature;
    private String ProfilePic;
    private Long buyNum;
    private Long sellNum;

    public ProfileDto(Member member, Long buyNum, Long sellNum) {
        this.username = member.getUserName();
        this.temperature = member.getTemperature();
        this.ProfilePic = member.getPicture().getUrl();
        this.buyNum = buyNum;
        this.sellNum = sellNum;
    }
}
