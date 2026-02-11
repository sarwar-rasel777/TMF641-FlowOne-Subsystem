package at.compax.reference.subsystem.tmf641.component.exceptions;

import java.io.Serial;

public class SimulationNotAllowedException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 27547555922394927L;

  public SimulationNotAllowedException(String message) {
    super(message);
  }

}
