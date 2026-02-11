package at.compax.reference.subsystem.tmf641.component.exceptions;

import java.io.Serial;

import at.compax.reference.subsystem.tmf641.component.model.Error;
import lombok.Getter;

@Getter
public class ConfiguredErrorException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 27547797922394927L;

  private final Error error;

  public ConfiguredErrorException(Error expectedError) {
    this.error = expectedError;
  }

}
