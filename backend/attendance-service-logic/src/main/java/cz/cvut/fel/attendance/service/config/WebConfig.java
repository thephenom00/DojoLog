package cz.cvut.fel.attendance.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("CORS settings applied");
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // TODO add specific vercel website before PROD release
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }

    /**
     * Adds interceptors to the registry.
     *
     * @param registry the InterceptorRegistry to add interceptors to
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Interceptor registered");
        registry.addInterceptor(new RequestInterceptor());
    }
}
