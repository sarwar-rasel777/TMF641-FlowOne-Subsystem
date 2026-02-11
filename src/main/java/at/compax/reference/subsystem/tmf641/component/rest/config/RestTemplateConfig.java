package at.compax.reference.subsystem.tmf641.component.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.reference.subsystem.tmf641.component.rest.Oauth2ClientCredentialFlowRestTemplate;
import at.compax.reference.subsystem.tmf641.component.rest.Oauth2ClientRestTemplate;
import at.compax.reference.subsystem.tmf641.component.rest.Oauth2PasswordFlowRestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

  @Value("${compax.connectTimeout:10000}")
  private int connectTimeout;
  @Value("${compax.connectionRequestTimeout:10000}")
  private int connectionRequestTimeout;
  @Value("${compax.readTimeout:30000}")
  private int readTimeout;

  @Autowired
  private final ObjectMapper objectMapper;

  @Bean
  @Primary
  @ConditionalOnProperty(name = "at.compax.oauth.client.provider.oauth.enabled", havingValue = "false", matchIfMissing = true)
  Oauth2ClientRestTemplate defaultRestTemplate(
      @Value("${FLOWONE_TOKEN_URL:[unset]}") String tokenUrl,
      @Value("${FLOWONE_CLIENT_ID:[unset]}") String clientId,
      @Value("${FLOWONE_CLIENT_SECRET:[unset]}") String clientSecret,
      @Value("${AUTH_USERNAME:[unset]}") String username,
      @Value("${AUTH_PASSWORD:[unset]}") String password) {
    Oauth2ClientRestTemplate restTemplate;
    if (username.equals("[unset]") || password.equals("[unset]")) {
      // Assuming there are no or missing credentials, then client_credentials flow will be used as fallback
      log.warn("There are missing credentials (username/password) - falling back to client_credentials flow, instead of password flow");
      restTemplate = new Oauth2ClientCredentialFlowRestTemplate(clientId,
          clientSecret,
          tokenUrl);
    } else {
      log.info("Client_id/client_secret and username/password supplied - using password flow");
      restTemplate = new Oauth2PasswordFlowRestTemplate(clientId,
          clientSecret,
          username,
          password,
          Oauth2PasswordFlowRestTemplate.ClientCredentialsLocation.HEADER,
          tokenUrl);
    }
    restTemplate.setRequestFactory(clientHttpRequestFactory());
    restTemplate.getMessageConverters().add(createMappingJacksonHttpMessageConverter());
    return restTemplate;
  }

  @Bean("wfmCallbackSimulationRestTemplate")
  @ConditionalOnProperty(name = "swagger.show-simulator-requests", havingValue = "true")
  Oauth2ClientRestTemplate wfmCallbackSimulationRestTemplate(
      @Value("${API_AUTH_URL:[unset]}") String tokenUrl,
      @Value("${OUTAGE_API_CLIENT_ID:[unset]}") String clientId,
      @Value("${OUTAGE_API_CLIENT_SECRET:[unset]}") String clientSecret) {
    Oauth2ClientRestTemplate restTemplate;
    restTemplate = new Oauth2ClientCredentialFlowRestTemplate(clientId, clientSecret, tokenUrl);
    restTemplate.setRequestFactory(clientHttpRequestFactory());
    restTemplate.getMessageConverters().add(createMappingJacksonHttpMessageConverter());
    return restTemplate;
  }

  private ClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
    requestFactory.setReadTimeout(readTimeout);
    return requestFactory;
  }

  private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(objectMapper);
    return converter;
  }

}
