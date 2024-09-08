package is.hi.hbv501g.Hugverk1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Hugverk1Application {

	public static void main(String[] args) {
		SpringApplication.run(Hugverk1Application.class, args);
	}

}
