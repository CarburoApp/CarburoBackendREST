package app.carburo.api.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Single source of truth for the exact browser origins allowed by CORS and by the web API-key policy.
 */
@Getter
@Component
public class CorsOriginPolicy {

    private final List<String> allowedOrigins;

    public CorsOriginPolicy(@Value("${cors.allowed-origins:https://carburo.app}") String configuredOrigins) {
        this.allowedOrigins = Arrays.stream(configuredOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    /**
     * Uses exact string matching: no wildcard or suffix/subdomain matching.
     */
    public boolean allows(String origin) {
        return StringUtils.hasText(origin) && allowedOrigins.contains(origin.trim());
    }
}
