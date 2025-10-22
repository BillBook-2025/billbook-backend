package BillBook_2025_backend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 API에 대해 허용
                        .allowedOrigins("http://localhost:3000") // 프론트엔드 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용 HTTP 메소드
                        .allowCredentials(true); // 쿠키 사용 허용
            }
        };
    }
}
