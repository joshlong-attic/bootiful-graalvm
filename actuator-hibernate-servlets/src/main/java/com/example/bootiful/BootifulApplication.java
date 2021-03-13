package com.example.bootiful;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class BootifulApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootifulApplication.class, args);
	}
}


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
class Customer {

	@Id
	@GeneratedValue
	private Integer id;

	private String name;

}

interface CustomerRepository extends JpaRepository<Customer, Integer> {
}

@Component
class CustomHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		return Health.status("I <3 Spring Boot!").build();
	}
}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

	private final CustomerRepository customerRepository;

	@GetMapping("/customers")
	Collection<Customer> get() {
		return this.customerRepository.findAll();
	}
}

@Component
@RequiredArgsConstructor
class CustomerRunner implements ApplicationRunner {

	private final CustomerRepository repos;

	@Override
	public void run(ApplicationArguments args) {
		List.of("A", "B", "C", "D")
			.stream()
			.map(name -> new Customer(null, name))
			.map(repos::save)
			.forEach(c -> System.out.println("saved " + c.getId() + '.'));
		repos.findAll().forEach(System.out::println);
	}
}