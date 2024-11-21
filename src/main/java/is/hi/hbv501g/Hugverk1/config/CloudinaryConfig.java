package is.hi.hbv501g.Hugverk1.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "donormatch",
                "api_key", "322724579848812",
                "api_secret", "l9TopxoZuA4MZcsY3pSr6XOvZs4"
        ));
    }
}