package at.compax.reference.subsystem.tmf641.component.rest;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import at.compax.reference.subsystem.tmf641.component.model.AuthServerResponse;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public abstract class Oauth2ClientRestTemplate extends RestTemplate {

  private final String grantType;
  private final String clientId;
  private final String clientSecret;
  private final String tokenUrl;
  private final String username;
  private final String password;
  private String token;
  private long expirationTime;

  public Oauth2ClientRestTemplate(String clientId, String clientSecret, String username, String password, String grantType, String tokenUrl) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.username = username;
    this.password = password;
    this.tokenUrl = tokenUrl;
    this.grantType = grantType;
  }

  public String getBearerToken() {
    if (token != null && !isExpired()) {
      return token;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    modifyHeadersForBearerToken(headers);

    Map<String, String> credentials = getBodyCredentialsForBearerToken();
    String payload = credentials.entrySet().stream()
        .filter(s -> s.getValue() != null)
        .map(s -> s.getKey() + "=" + URLEncoder.encode(s.getValue(), Charset.defaultCharset()))
        .collect(Collectors.joining("&"));
    RequestEntity<Object> requestEntity = RequestEntity.method(HttpMethod.POST, tokenUrl).headers(headers).body(payload);

    ResponseEntity<AuthServerResponse> response = super.exchange(requestEntity, new ParameterizedTypeReference<>() {});
    AuthServerResponse authServerResponse = response.getBody();

    if (authServerResponse != null && StringUtils.isNotBlank(authServerResponse.expiresIn) && StringUtils.isNumeric(authServerResponse.expiresIn)) {
      expirationTime = Instant.now().plusSeconds(Long.parseLong(authServerResponse.expiresIn)).getEpochSecond() - 5;
      log.info("Token refreshed now. New token expires at: {}", expirationTime);
    }
    token = Objects.requireNonNull(authServerResponse).accessToken;

    return token;
  }

  protected Map<String, String> getBodyCredentialsForBearerToken() {
    Map<String, String> credentials = new LinkedHashMap<>();
    credentials.put("grant_type", grantType);
    credentials.put("username", username);
    credentials.put("password", password);
    credentials.put("client_id", clientId);
    credentials.put("client_secret", clientSecret);
    return credentials;
  }

  private boolean isExpired() {
    return Instant.now().getEpochSecond() > expirationTime;
  }

  protected void modifyHeadersForBearerToken(HttpHeaders headers) {

  }

}
