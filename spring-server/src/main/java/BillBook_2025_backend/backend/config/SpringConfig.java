package BillBook_2025_backend.backend.config;

import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.MemoryBookRepository;
import BillBook_2025_backend.backend.repository.MemoryUserRepository;
import BillBook_2025_backend.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {
    private final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public UserRepository userRepository() {
        return new MemoryUserRepository();
    }

    @Bean
    public BookRepository bookRepository() {
        return new MemoryBookRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Postman 같은 외부에서 POST 테스트할 수 있도록 CSRF 비활성화
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/**").permitAll()  // 회원가입/로그인 API는 인증 없이 허용
                        .anyRequest().authenticated()  // 그 외는 인증 필요
                );

        return http.build();
    }
}
