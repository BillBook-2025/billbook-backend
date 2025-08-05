package BillBook_2025_backend.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userId;
    private String password;
    private String email;
    private double temperature;
    private String userName;

    @OneToMany
    private List<Search> search = new ArrayList<>();

    // 보낸 결제 목록
    @OneToMany(mappedBy = "sender")
    private List<Payment> sentPayments = new ArrayList<>();

    // 받은 결제 목록
    @OneToMany(mappedBy = "receiver")
    private List<Payment> receivedPayments = new ArrayList<>();

    @OneToMany
    private List<Follow> follower = new ArrayList<>();

    @OneToMany
    private List<Follow> following = new ArrayList<>();

    @OneToMany
    private List<Image> images = new ArrayList<>();

    @OneToMany
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany
    private List<Message> messages = new ArrayList<>();



    protected User() {}

    public User(String userId, String password, String email, String username) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userName = username;
        this.temperature = 36.5;

    }
}
