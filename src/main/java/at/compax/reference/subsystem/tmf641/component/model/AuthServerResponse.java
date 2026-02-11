package at.compax.reference.subsystem.tmf641.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthServerResponse {

  @JsonProperty("access_token")
  public String accessToken;
  @JsonProperty("refresh_token")
  public String refreshToken;
  @JsonProperty("token_type")
  public String tokenType;
  @JsonProperty("expires_in")
  public String expiresIn;
  @JsonProperty("scope")
  public String scope;
  @JsonProperty("error")
  public String error;
  @JsonProperty("error_description")
  public String errorDescription;
  @JsonProperty("instance_url")
  public String instanceUrl;

}