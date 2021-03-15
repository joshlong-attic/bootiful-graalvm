package nativex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.nativex.hint.ProxyHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.retrosocket.EnableRSocketClients;
import org.springframework.retrosocket.RSocketClientBuilder;
import reactor.core.publisher.Mono;

/**
	* The custom component scanning doesn't quite work with
	* Spring Native just yet, so we need to manually register
	* a client interface using the
	*/
@ProxyHint (
	types = {
		GreetingClient.class,
		org.springframework.aop.SpringProxy.class,
		org.springframework.aop.framework.Advised.class,
		org.springframework.core.DecoratingProxy.class

	}
)
@TypeHint(
	typeNames = {
		"org.springframework.retrosocket.RSocketClientsRegistrar",
	},
	types = {
		Greeting.class,
		GreetingClient.class,
		ReusableMessageFactory.class,
		DefaultFlowMessageFactory.class
	})
@EnableRSocketClients
@SpringBootApplication
public class RetrosocketNativeApplication {

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(RetrosocketNativeApplication.class, args);
		Thread.sleep(5_000);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(GreetingClient rgc) {
		return event -> {
			Mono<Greeting> greet = rgc.greet("Spring fans!");
			greet.subscribe(gr -> System.out.println("response: " + gr.toString()));
		};
	}

	@Bean
	RSocketRequester rSocketRequester(
		@Value("${service.host:localhost}") String host,
		RSocketRequester.Builder builder) {
		return builder.tcp(host, 8181);
	}

	@Bean
	GreetingClient greetingClient(RSocketRequester rsr) {
		var rcfb = new RSocketClientBuilder();
		return rcfb.buildClientFor(GreetingClient.class, rsr);
	}

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Greeting {
	private String message;
}


interface GreetingClient {

	@MessageMapping("greeting.{name}")
	Mono<Greeting> greet(@DestinationVariable String name);
}


