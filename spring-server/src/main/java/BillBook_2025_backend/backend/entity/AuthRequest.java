package BillBook_2025_backend.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String userId;
    private String password;
    private String confirmPassword;
}
