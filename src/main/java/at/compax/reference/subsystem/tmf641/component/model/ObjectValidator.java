package at.compax.reference.subsystem.tmf641.component.model;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.reference.subsystem.tmf641.component.exceptions.Tmf641FlowOneException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ObjectValidator {

  private final ObjectMapper objectMapper;
  private final Validator validator;

  public <T> T readAndValidateObject(PayloadSendingModel model, Class<T> clazz) {
    try {
      return validateObject(objectMapper.readValue(model.getPayload(), clazz));
    } catch (IOException e) {
      throw new Tmf641FlowOneException(String.format("Error deserializing %s", model), e);
    }
  }

  public <T> T validateObject(T obj) {
    handleConstraintViolations(validator.validate(obj));
    return obj;
  }

  private <T> void handleConstraintViolations(Set<ConstraintViolation<T>> violations) {
    if (violations.isEmpty()) {
      return;
    }
    String msg = violations.stream() //
        .map(violation -> violation.getRootBeanClass().getName() + ": " + violation.getPropertyPath() + " " + violation.getMessage()) //
        .sorted() //
        .collect(Collectors.joining(", "));
    throw new IllegalArgumentException(msg);
  }

}
