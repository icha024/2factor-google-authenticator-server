package com.clianz.authotp;

import com.clianz.authotp.google.PasscodeGenerator;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.logging.Logger;

import static com.clianz.authotp.google.PasscodeGenerator.getSigningOracle;

public class AuthOtpApplication {

	private final static Logger LOGGER = Logger.getLogger(AuthOtpApplication.class.getName());

	private static final String JSON_CONTENT_TYPE = "application/json";
	public static final String RESULT_SUCCESS = "{\"result\":\"success\"}";
	public static final String RESULT_FAILED = "{\"result\":\"failed\"}";

	public static void main(final String[] args) {
		new AuthOtpApplication().start();
	}

	public void start() {
		Integer port = getEnvProperty("PORT", 8080);
		Integer pastInterval = getEnvProperty("otp.interval.past", 0);
		Integer futureInterval = getEnvProperty("otp.interval.future", 0);
		Integer passcodeLength = getEnvProperty("otp.passcode.length", PasscodeGenerator.PASS_CODE_LENGTH);

		LOGGER.info("Listening to port: " + port);
		LOGGER.info("Max past interval: " + pastInterval);
		LOGGER.info("Max future interval: " + futureInterval);
		LOGGER.info("Passcode length: " + passcodeLength);

		Undertow server = Undertow.builder().addHttpListener(port, "0.0.0.0")
				.setHandler(Handlers.pathTemplate().add("/{key}/{code}", (AsyncHttpHandler) exchange -> {
					String key = exchange.getQueryParameters().get("key").getFirst();
					String code = exchange.getQueryParameters().get("code").getFirst();
					PasscodeGenerator.Signer signer = getSigningOracle(key);
					PasscodeGenerator passcodeGenerator = new PasscodeGenerator(signer, passcodeLength);

					long time = System.currentTimeMillis() / 1000 / PasscodeGenerator.INTERVAL;
					boolean success = passcodeGenerator.verifyTimeoutCode(code, time, futureInterval, pastInterval);

					exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, JSON_CONTENT_TYPE);
					if (success) {
						exchange.getResponseSender().send(RESULT_SUCCESS);
						//LOGGER.info("Verification success.");
					} else {
						exchange.getResponseSender().send(RESULT_FAILED);
						//LOGGER.info("Verification failed.");
					}
				})).build();
		server.start();
	}

	private Integer getEnvProperty(String propName, Integer defaultVal) {
		String propVal = System.getenv(propName);
		return (propVal == null) ? Integer.getInteger(propName, defaultVal) : Integer.parseInt(propVal);
	}

	@FunctionalInterface
	interface AsyncHttpHandler extends HttpHandler {
		default void handleRequest(HttpServerExchange exchange) throws Exception {
			// non-blocking
			if (exchange.isInIoThread()) {
				exchange.dispatch(this);
				return;
			}
			// handler code
			asyncBlockingHandler(exchange);
		}

		void asyncBlockingHandler(HttpServerExchange exchange) throws Exception;
	}
}