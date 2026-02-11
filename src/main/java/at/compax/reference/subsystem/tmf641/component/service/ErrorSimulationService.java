package at.compax.reference.subsystem.tmf641.component.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

import at.compax.reference.subsystem.tmf641.component.exceptions.ConfiguredErrorException;
import at.compax.reference.subsystem.tmf641.component.model.Error;

@Service
public class ErrorSimulationService {

  private Error expectedError;
  private Iterations remainingIterations;

  public void setExpectedError(Error Error, int iterations, boolean toggled) {
    this.expectedError = Error;
    this.remainingIterations = toggled ? Iterations.toggled() : Iterations.of(iterations);
  }

  public Optional<Error> peekExpectedError() {
    return Optional.ofNullable(expectedError);
  }

  public void clearExpectedError() {
    this.expectedError = null;
    this.remainingIterations = null;
  }

  public void checkForExpectedError() throws ConfiguredErrorException {
    if (expectedError == null) {
      return;
    }
    if (remainingIterations.toggled) {
      throw new ConfiguredErrorException(expectedError);
    }
    ConfiguredErrorException exception = new ConfiguredErrorException(expectedError);
    if (!remainingIterations.decreaseIterations()) {
      expectedError = null;
    }
    throw exception;
  }

  public static class Iterations {
    private int remainingIterations;
    private final boolean toggled;

    public static Iterations of(int iterations) {
      return new Iterations(iterations, false);
    }

    public static Iterations toggled() {
      return new Iterations(0, true);
    }

    private Iterations(int iterations, boolean toggled) {
      this.remainingIterations = iterations;
      this.toggled = toggled;
    }

    public boolean decreaseIterations() {
      return --remainingIterations > 0;
    }
  }

}
