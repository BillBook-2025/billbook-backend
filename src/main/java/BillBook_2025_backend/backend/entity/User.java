package BillBook_2025_backend.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private Long id;
    private String userId;
    private String password;
    private String email;
    private double temperature;
    private boolean isPhoneVerified;
    private String userName;

    public User(String userId, String password, String email, String username) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.userName = username;
        this.temperature = 36.5;

    }
}
