package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowDto {
    Long userId;
    String userName;
    String profilePic;

    public FollowDto(Member member) {
        this.userId = member.getId();
        this.userName = member.getUserName();
        this.profilePic = member.getImage().getUrl();
    }
}
