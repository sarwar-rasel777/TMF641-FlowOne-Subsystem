package at.compax.reference.subsystem.tmf641.component.exceptions;

import java.io.Serial;
import java.util.UUID;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Tmf641FlowOneException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 27547797920004927L;

  private final HttpStatus statusCode;

  private final String errorId;

  public Tmf641FlowOneException(HttpStatus statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
    errorId = errorId();
  }

  public Tmf641FlowOneException(String message, Throwable cause) {
    this(null, message, cause);
  }

  public Tmf641FlowOneException(HttpStatus statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
    errorId = errorId();
  }

  public static String errorId() {
    return UUID.randomUUID().toString().substring(0, 6);
  }

}
