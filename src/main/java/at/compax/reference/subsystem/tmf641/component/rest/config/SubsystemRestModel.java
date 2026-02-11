package at.compax.reference.subsystem.tmf641.component.rest.config;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import org.springframework.http.HttpHeaders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubsystemRestModel<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 98732597239829333L;

  private URI url;
  private HttpHeaders headers;
  private String method;
  private String externalId;
  private T body;

}
