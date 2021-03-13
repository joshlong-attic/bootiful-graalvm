package com.example;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
@TypeHint(types = Reservation.class)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ReservationDao dao) {
		return args -> Arrays
			.asList("spring", "spring boot", "spring cloud", "doma")
			.forEach(s -> {
				var r = new Reservation();
				r.name = s;
				dao.insert(r);
			});
	}
}


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
class ReservationRestController {

	private final ReservationDao dao;

	@GetMapping("/search")
	Collection<Reservation> search(@RequestParam String name) {
		return this.dao.selectByName(name);
	}

	@GetMapping
	Collection<Reservation> get() {
		return this.dao.selectAll();
	}
}


