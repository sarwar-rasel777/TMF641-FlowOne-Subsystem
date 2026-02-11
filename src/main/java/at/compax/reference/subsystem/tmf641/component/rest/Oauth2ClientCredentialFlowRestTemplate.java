package at.compax.reference.subsystem.tmf641.component.rest;

public class Oauth2ClientCredentialFlowRestTemplate extends Oauth2ClientRestTemplate {

  public Oauth2ClientCredentialFlowRestTemplate(String clientId, String clientSecret, String tokenUrl) {
    super(clientId, clientSecret, null, null, "client_credentials", tokenUrl);
  }

}
