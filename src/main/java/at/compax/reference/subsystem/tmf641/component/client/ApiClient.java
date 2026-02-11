package at.compax.reference.subsystem.tmf641.component.client;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import at.compax.reference.subsystem.tmf641.component.rest.Oauth2ClientRestTemplate;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestModel;
import at.compax.reference.subsystem.tmf641.component.rest.config.SubsystemRestResponseModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApiClient {

  @Value("${custom.openapi.security-scheme.token-url}")
  private String authUrl;

  @Autowired
  private ClientConfig clientConfig;
  @Autowired
  private Oauth2ClientRestTemplate restTemplate;

  public UriComponentsBuilder buildBaseUriHub() {
    return UriComponentsBuilder.fromUriString(clientConfig.getConfig().getHost() + File.separator + "hub");
  }

  public UriComponentsBuilder buildBaseUriServiceOrder() {
    return UriComponentsBuilder.fromUriString(clientConfig.getConfig().getHost() + File.separator + "serviceorder");
  }

  protected UriComponentsBuilder buildBaseUriCancelServiceOrder() {
    return UriComponentsBuilder.fromUriString(clientConfig.getConfig().getHost() + File.separator + "cancelserviceorder");
  }

  public SubsystemRestResponseModel<Void> exchange(SubsystemRestModel<?> request) {
    HttpHeaders modifiedHeaders = request.getHeaders();
    if (!modifiedHeaders.containsKey(HttpHeaders.AUTHORIZATION) && !verifyEnvironment()) {
      modifyHeadersForBearerToken(modifiedHeaders);
    }
    ResponseEntity<Void> exchange = restTemplate.exchange(request.getUrl(), HttpMethod.valueOf(request.getMethod()), new HttpEntity<>(request.getBody(), modifiedHeaders), Void.class);
    return new SubsystemRestResponseModel<>(exchange.getHeaders(), exchange.getStatusCode(), exchange.getStatusCode().value(), null);
  }

  public HttpHeaders buildBaseHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  public URI buildInstallationCallbackUri() {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(clientConfig.getConfig().getCallbackHost() + "/wfm/response").pathSegment();
    return uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();
  }

  public URI buildBaseUriHubId(Integer id) {
    UriComponentsBuilder uriBuilder = this.buildBaseUriHub().pathSegment(String.valueOf(id));
    return uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();
  }

  public URI buildBaseUriForServiceOrders() {
    UriComponentsBuilder uriBuilder = this.buildBaseUriServiceOrder();
    return uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();
  }

  public URI buildBaseUriForServiceAmend(String flowoneId) {
    UriComponentsBuilder uriBuilder = this.buildBaseUriServiceOrder().pathSegment(String.valueOf(flowoneId));
    return uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();
  }

  public URI buildBaseUriCancelServiceOrderWithFlowoneId(String flowoneId) {
    UriComponentsBuilder uriBuilder = this.buildBaseUriCancelServiceOrder().pathSegment(String.valueOf(flowoneId));
    return uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();
  }


  private void modifyHeadersForBearerToken(HttpHeaders headers) {
    String token = restTemplate.getBearerToken();
    String authorizationHeader = ("Bearer " + token);
    headers.add(HttpHeaders.AUTHORIZATION, authorizationHeader);
  }

  private boolean verifyEnvironment() {
    if (authUrl == null) {
      return false;
    }
    return authUrl.contains("tlint") || authUrl.contains("tlint2");
  }

}
