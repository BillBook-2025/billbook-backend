package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowDto {
    Long userId;
    String userName;
    String profilePic;

    public FollowDto(Member member) {
        this.userId = member.getId();
        this.userName = member.getUserName();
        if(member.getPicture() != null) {
            this.profilePic = member.getPicture().getUrl();
        }
    }
}
