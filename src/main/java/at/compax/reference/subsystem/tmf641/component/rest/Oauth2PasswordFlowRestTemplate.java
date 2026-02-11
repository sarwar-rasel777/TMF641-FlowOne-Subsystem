package at.compax.reference.subsystem.tmf641.component.rest;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import com.nimbusds.jose.util.Base64;

public class Oauth2PasswordFlowRestTemplate extends Oauth2ClientRestTemplate {

  private final ClientCredentialsLocation clientCredentialsLocation;

  public Oauth2PasswordFlowRestTemplate(String clientId, String clientSecret, String username, String password, ClientCredentialsLocation clientCredentialsLocation, String tokenUrl) {
    super(clientId, clientSecret, username, password, "password", tokenUrl);
    this.clientCredentialsLocation = clientCredentialsLocation;
  }

  @Override
  protected Map<String, String> getBodyCredentialsForBearerToken() {
    Map<String, String> credentials = new LinkedHashMap<>();
    credentials.put("grant_type", getGrantType());
    credentials.put("username", getUsername());
    credentials.put("password", getPassword());
    if (!clientCredentialsLocation.equals(ClientCredentialsLocation.HEADER)) {
      credentials.put("client_id", getClientId());
      credentials.put("client_secret", getClientSecret());
    }
    return credentials;
  }

  @Override
  protected void modifyHeadersForBearerToken(HttpHeaders headers) {
    if (clientCredentialsLocation.equals(ClientCredentialsLocation.HEADER)) {
      String authorizationHeader = "Basic " + Base64.encode(getClientId() + ":" + getClientSecret());
      headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
    }
  }

  public enum ClientCredentialsLocation {
    HEADER,
    BODY
  }

}
