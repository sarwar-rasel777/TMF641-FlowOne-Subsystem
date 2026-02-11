package at.compax.reference.subsystem.tmf641.component.exceptions;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import at.compax.reference.subsystem.tmf641.component.model.Error;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(ConfiguredErrorException.class)
  public ResponseEntity<Error> toErrorEntity(ConfiguredErrorException e) {
    Error error = new Error();
    String receivedCode = e.getError().getCode();
    error.setCode(e.getError().getCode());
    error.setReason(e.getError().getReason());
    int errorCode = NumberUtils.isCreatable(receivedCode) ? Integer.parseInt(receivedCode) : HttpStatus.INTERNAL_SERVER_ERROR.value();
    return ResponseEntity.status(errorCode).body(error);
  }

  @ExceptionHandler(SimulationNotAllowedException.class)
  public ResponseEntity<Error> simulationNotAllowed(SimulationNotAllowedException e) {
    Error error = new Error();
    error.setCode(HttpStatus.BAD_REQUEST.toString());
    error.setReason(e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> otherExceptions(Exception e) {
    log.error("{}", e.getMessage(), e);
    Error error = new Error();
    error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
    error.setReason(e.getClass().getSimpleName());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

}
