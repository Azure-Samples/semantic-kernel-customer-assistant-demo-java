package com.microsoft.semantickernel.sample.java.sk.assistant;

import com.azure.core.credential.KeyCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.AzureCliCredentialBuilder;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@ApplicationScoped
public class AuthenticationProvider {
    private final String clientId;
    private final String profile;
    private final String openaiKey;
    private final String tenantId;

    @Inject
    public AuthenticationProvider(
            @ConfigProperty(name = "profile", defaultValue = "deploy")
            String profile,
            @ConfigProperty(name = "azure.client.secret", defaultValue = "")
            Optional<String> openaiKey,
            @ConfigProperty(name = "azure.client.id", defaultValue = "system-managed-identity")
            String clientId,
            @ConfigProperty(name = "azure.tenant.id", defaultValue = "")
            Optional<String> tenantId
    ) {
        this.profile = profile;
        this.clientId = clientId.isEmpty() ? "system-managed-identity" : clientId;
        this.openaiKey = openaiKey.orElse(null);
        this.tenantId = tenantId.orElse(null);
    }

    public TokenCredential getCredentials() {
        if ("dev".equals(profile)) {
            return new AzureCliCredentialBuilder().build();
        } else if ("devEnv".equals(profile)) {
            return new ClientSecretCredentialBuilder()
                    .clientSecret(openaiKey)
                    .tenantId(tenantId)
                    .clientId(clientId)
                    .build();
        } else if ("docker".equals(profile)) {
            return new EnvironmentCredentialBuilder().build();
        }

        if ("system-managed-identity".equals(clientId)) {
            new ManagedIdentityCredentialBuilder().build();
        }
        return new ManagedIdentityCredentialBuilder().clientId(this.clientId).build();
    }

    public KeyCredential getKeyCredential() {
        return new KeyCredential(openaiKey);
    }
}
