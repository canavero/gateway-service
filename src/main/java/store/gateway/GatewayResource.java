package store.gateway;

import java.lang.management.ManagementFactory;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayResource {

    private static final String HOSTNAME = System.getenv().getOrDefault("HOSTNAME", "unknown");

    // só o hostname: mostra qual réplica respondeu (nunca devolva System.getenv() inteiro)
    @GetMapping("/health-check")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("hostname", HOSTNAME));
    }

    // informações da aplicação (alvo do teste de carga / HPA)
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "application", "store-gateway",
            "version", System.getenv().getOrDefault("APP_VERSION", "1.0.0"),
            "hostname", HOSTNAME,
            "java", System.getProperty("java.version"),
            "uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime(),
            "processors", Runtime.getRuntime().availableProcessors()
        ));
    }

}
