package com.example.rx1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.nativex.hint.InitializationHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@TypeHint(types = {
	ReactorNettyRequestUpgradeStrategy.class,
	Customer.class}
)
@SpringBootApplication
public class Rx1Application {

	private final ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(Rx1Application.class, args);
	}

	Rx1Application(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Bean
	SimpleUrlHandlerMapping body(WebSocketHandler wsh) {
		return new SimpleUrlHandlerMapping(Map.of("/ws/greetings", wsh), 10);
	}

	@SneakyThrows
	private String toJson(Object o) {
		return this.objectMapper.writeValueAsString(o);
	}

	@Bean
	WebSocketHandler webSocketHandler() {
		return webSocketSession -> {
			var ongoing = Flux
				.fromStream(Stream.generate(() -> Map.of("greeting", "Hello, world @ " + Instant.now() + "!")))
				.delayElements(Duration.ofSeconds(1))
				.map(this::toJson)
				.map(webSocketSession::textMessage);
			return webSocketSession.send(ongoing);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(CustomerRepository customerRepository) {
		return route()
			.GET("/customers", r -> ok().body(customerRepository.findAll(), Customer.class))
			.build();
	}


	@Bean
	ApplicationRunner runner(
		DatabaseClient dbc,
		CustomerRepository customerRepository) {
		return args -> {
			var ddl = dbc
				.sql("create table customer(id serial primary key,name varchar (255) not null)")
				.fetch()
				.rowsUpdated();
			var names = Flux.just("A", "B", "C", "D")
				.map(name -> new Customer(null, name))
				.flatMap(cu -> customerRepository.save(cu));
			var all = customerRepository.findAll();
			ddl.thenMany(names).thenMany(all).subscribe(System.out::println);
		};
	}
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

	@Id
	private Integer id;
	private String name;
}
