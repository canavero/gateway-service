package store.gateway.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter implements GlobalFilter {

    public static final String AUTH_COOKIE_TOKEN = "__store_jwt_token";

    @Autowired
    private RouterValidator routerValidator;

    @Value("${store.auth.solve-url:http://auth:8080/auth/solve}")
    private String solveUrl;

    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        if (!routerValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }
        HttpCookie cookie = request.getCookies().getFirst(AUTH_COOKIE_TOKEN);
        if (cookie == null || cookie.getValue().isBlank()) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        }
        // pergunta ao auth de quem é o token e repassa só o id da conta (header id-account)
        return webClient.post()
            .uri(solveUrl)
            .bodyValue(Map.of("token", cookie.getValue().trim()))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError,
                r -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")))
            .bodyToMono(Map.class)
            .flatMap(body -> {
                Object idAccount = body.get("idAccount");
                if (idAccount == null) return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                return chain.filter(exchange.mutate()
                    .request(request.mutate().header("id-account", idAccount.toString()).build())
                    .build());
            });
    }

}
