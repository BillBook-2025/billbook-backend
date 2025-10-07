package BillBook_2025_backend.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userId;
    private String password;
    private String email;
    private double temperature;
    private String userName;

    //추가됨
    private String realName;
    private String phone;
    private String address;
    private String post_code;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private UserPoints userPoints;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Picture picture;

    @OneToMany(mappedBy = "member")
    private List<Search> search = new ArrayList<>();

    // 보낸 결제 목록
    @OneToMany(mappedBy = "buyer")
    private List<Payment> sentPayments = new ArrayList<>();

    // 받은 결제 목록
    @OneToMany(mappedBy = "seller")
    private List<Payment> receivedPayments = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private List<Follow> follower = new ArrayList<>();

    @OneToMany(mappedBy = "follower")
    private List<Follow> following = new ArrayList<>();


    @OneToMany(mappedBy = "seller")
    private List<ChatRoom> sellingRooms;

    @OneToMany(mappedBy = "buyer")
    private List<ChatRoom> buyingRooms;

    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();



    protected Member() {}

    public Member(String userId, String password, String email, String username) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userName = username;
        this.temperature = 36.5;

    }
}
