package com.clerodri.web;


import com.clerodri.core.domain.model.RolEnum;
import com.clerodri.details.entity.UserEntity;
import com.clerodri.details.repository.jpa.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;




@ComponentScan(basePackages = {"com.clerodri"})
@EntityScan(basePackages = "com.clerodri.details.entity")
@EnableJpaRepositories(basePackages = "com.clerodri.details.repository")
@SpringBootApplication(scanBasePackages = {"com.clerodri"})
public class ReservationWebApplication {


	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ReservationWebApplication.class);
		Environment env = app.run(args).getEnvironment();
		System.out.println("Active Profiles: " + String.join(", ", env.getActiveProfiles()));
	}

	@Bean
	CommandLineRunner init(UserJpaRepository userRepository) {
		return args -> {

			// Create admin user
			UserEntity admin = UserEntity.builder()
					.username("admin")
					.password("$2a$10$zJz7H1XdTjaC38f.i.mble48ufKAK82VKRgYPOAYGjor5LEdEOzVO")
					.role(RolEnum.ADMIN)
					.email("admin@gmail.com")
					.build();
			userRepository.save(admin);

			// Create user 1
			UserEntity roro = UserEntity.builder()
					.username("roro")
					.password("$2a$10$zJz7H1XdTjaC38f.i.mble48ufKAK82VKRgYPOAYGjor5LEdEOzVO")
					.role(RolEnum.USER)
					.email("roro@gmail.com")
					.build();
			userRepository.save(roro);

		};
	}


}
