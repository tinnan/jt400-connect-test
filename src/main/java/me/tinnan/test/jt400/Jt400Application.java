package me.tinnan.test.jt400;

import com.ibm.as400.access.AS400;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Jt400Application {

	public static void main(String[] args) {

		SpringApplication.run(Jt400Application.class, args);
	}

}
