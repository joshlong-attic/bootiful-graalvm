package nativex;

import lombok.extern.log4j.Log4j2;
import org.springframework.nativex.type.ComponentProcessor;
import org.springframework.nativex.type.NativeContext;

import java.util.List;

/**
	* we need to register any interface that has {@code @RSocketClient} on it.
	*/
@Log4j2
class RetrosocketComponentProcessor implements ComponentProcessor {

	@Override
	public boolean handle(NativeContext nativeContext, String className, List<String> list) {

		var x = "RETROSOCKET: should we handle " + nativeContext.getTypeSystem()
			.resolve(className).getDottedName();
		System.out.println(x);
		System.err.println(x);
		log.info(x);
		return true;
	}

	@Override
	public void process(NativeContext nativeContext, String s, List<String> list) {


	}
}
