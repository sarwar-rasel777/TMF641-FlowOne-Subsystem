package at.compax.reference.subsystem.tmf641.component.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.foundation.subsystem.service.component.model.ValuesConsumerFactory;
import at.compax.foundation.subsystem.service.component.service.CachedSubsystemConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static at.compax.reference.subsystem.tmf641.component.constants.ServiceCharacteristicNames.CLIENT_REGISTRATION_ID;

@Component
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientConfig {

  private final CachedSubsystemConfiguration configuration;
  private final ValuesConsumerFactory valuesConsumerFactory;

  public ValuesConsumer getItos() {
    return valuesConsumerFactory.consumer(configuration.getConfiguration().getConfigs());
  }

  public Config getConfig() {
    return Config.builder().host(this.getItos().stringValue("API_BASE_URI")).callbackHost(this.getItos().stringValue("FLOWONE_CALLBACK_URI")).build();
  }

  @Bean
  ClientRegistration flowoneClientRegistration(
      @Value("${FLOWONE_TOKEN_URL}") String tokenUrl,
      @Value("${FLOWONE_CLIENT_ID}") String clientId) {
    return ClientRegistration.withRegistrationId(CLIENT_REGISTRATION_ID)
        .clientId(clientId)
        .tokenUri(tokenUrl)
        .authorizationGrantType(new AuthorizationGrantType("password")).build();
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration flowOneClientRegistration) {
    return new InMemoryClientRegistrationRepository(flowOneClientRegistration);
  }

  @Bean
  public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
    return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
  }

  @Bean
  public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();
    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    return authorizedClientManager;
  }

  @Getter
  @Builder
  public static class Config {
    private String host;
    private String callbackHost;
  }

}
