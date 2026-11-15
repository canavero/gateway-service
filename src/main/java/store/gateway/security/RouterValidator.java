package store.gateway.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

    // rotas abertas ("METHOD /path", aceita /** no fim); todo o resto exige o cookie JWT
    private final List<String> openApiEndpoints = List.of(
        "POST /auth/register",
        "POST /auth/login",
        "GET /auth/logout"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream().noneMatch(uri -> {
        String[] parts = uri.split(" ");
        String method = parts[0];
        String path = parts[1];
        String requested = request.getURI().getPath();
        boolean deep = path.endsWith("/**");
        return ("ANY".equalsIgnoreCase(method) || request.getMethod().name().equalsIgnoreCase(method))
            && (requested.equals(path) || (deep && requested.startsWith(path.replace("/**", ""))));
    });

}
