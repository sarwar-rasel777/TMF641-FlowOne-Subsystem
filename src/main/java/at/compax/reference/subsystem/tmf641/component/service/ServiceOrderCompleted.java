package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.foundation.subsystem.service.component.model.ValuesBuilderFactory;
import at.compax.foundation.subsystem.service.component.service.ReceivingSender;
import at.compax.reference.subsystem.tmf641.component.model.ObjectSerializer;
import at.compax.reference.subsystem.tmf641.component.model.ObjectValidator;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderStateType;
import at.compax.reference.subsystem.tmf641.component.translator.EventStateChangeEventTranslator;

@Service("serviceOrderCompleted")
public class ServiceOrderCompleted extends ServiceOrderStateChangeEventService {

  @Autowired
  public ServiceOrderCompleted(EventStateChangeEventTranslator eventTranslator, ValuesBuilderFactory valuesBuilderFactory, ObjectSerializer objectSerializer, ObjectMapper objectMapper, ObjectValidator objectValidator,
      @Lazy ReceivingSender receivingSender) {
    super(eventTranslator, valuesBuilderFactory, objectSerializer, objectMapper, objectValidator, receivingSender);
  }

  @Override
  public ServiceOrderStateType supportedState() {
    return ServiceOrderStateType.COMPLETED;
  }

}
