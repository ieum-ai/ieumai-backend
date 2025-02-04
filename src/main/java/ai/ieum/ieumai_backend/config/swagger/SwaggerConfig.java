package ai.ieum.ieumai_backend.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IEUMAI API Documentation")
                        .description("IEUMAI 프로젝트의 API 문서")
                        .version("1.0.0"));
    }
}
