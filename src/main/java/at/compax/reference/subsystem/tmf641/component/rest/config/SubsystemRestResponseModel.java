package at.compax.reference.subsystem.tmf641.component.rest.config;

import java.io.Serial;
import java.io.Serializable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubsystemRestResponseModel<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 982345628367872632L;

  private HttpHeaders headers;
  private HttpStatusCode httpStatus;
  private int httpStatusCode;
  private T body;

}
