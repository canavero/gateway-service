package store.gateway.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

class RouterValidatorTest {

    private final RouterValidator validator = new RouterValidator();

    @Test
    void loginAndRegisterAreOpen() {
        assertFalse(validator.isSecured.test(MockServerHttpRequest.post("/auth/login").build()));
        assertFalse(validator.isSecured.test(MockServerHttpRequest.post("/auth/register").build()));
        assertFalse(validator.isSecured.test(MockServerHttpRequest.get("/auth/logout").build()));
    }

    @Test
    void everythingElseIsSecured() {
        assertTrue(validator.isSecured.test(MockServerHttpRequest.get("/accounts").build()));
        assertTrue(validator.isSecured.test(MockServerHttpRequest.get("/exchanges/USD/BRL").build()));
        assertTrue(validator.isSecured.test(MockServerHttpRequest.get("/auth/login").build())); // método errado
        assertTrue(validator.isSecured.test(MockServerHttpRequest.post("/auth/solve").build()));
    }

}
