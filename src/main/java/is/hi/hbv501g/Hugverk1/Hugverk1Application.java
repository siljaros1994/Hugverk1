package is.hi.hbv501g.Hugverk1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "is.hi.hbv501g.Hugverk1")
public class Hugverk1Application {
	public static void main(String[] args) {
		SpringApplication.run(Hugverk1Application.class, args);
	}
}