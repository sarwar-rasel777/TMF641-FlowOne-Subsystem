package at.compax.reference.subsystem.tmf641.component.model;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.reference.subsystem.tmf641.component.exceptions.Tmf641FlowOneException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ObjectSerializer {

  private final ObjectMapper objectMapper;

  public byte[] toBytes(Object o) {
    try {
      return objectMapper.writeValueAsBytes(o);
    } catch (JsonProcessingException e) {
      throw new Tmf641FlowOneException(String.format("Error serializing object %s", o), e);
    }
  }

}
