package com.example.rsocketclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.nativex.hint.TypeHint;

import java.net.URI;
import java.net.URL;

@TypeHint(types = Greeting.class)
@SpringBootApplication
public class RsocketClientApplication {

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(RsocketClientApplication.class, args);
		Thread.sleep(1000);
	}

	@Bean
	RSocketRequester rSocketRequester(
		@Value("${service.host}") String hostProperty,
		RSocketRequester.Builder builder) throws Exception {
		var url = URI.create(hostProperty);
		var host = url.getHost();
		var port = url.getPort();
		return builder.tcp(host, port);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(RSocketRequester rSocketRequester) {
		return event -> rSocketRequester
			.route("greeting.world")
			.retrieveMono(Greeting.class)
			.subscribe(System.out::println);
	}

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Greeting {
	private String message;
}