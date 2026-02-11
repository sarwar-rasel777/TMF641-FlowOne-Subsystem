package at.compax.reference.subsystem.tmf641.component.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.foundation.subsystem.service.component.model.ValuesBuilderFactory;
import at.compax.foundation.subsystem.service.component.service.ReceivingSender;
import at.compax.reference.subsystem.tmf641.component.model.ObjectSerializer;
import at.compax.reference.subsystem.tmf641.component.model.ObjectValidator;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderStateType;
import at.compax.reference.subsystem.tmf641.component.model.StateChangeEvent;
import at.compax.reference.subsystem.tmf641.component.translator.EventStateChangeEventTranslator;

@Service("serviceOrderInProgress")
public class ServiceOrderInProgress extends ServiceOrderStateChangeEventService {

  @Autowired
  public ServiceOrderInProgress(EventStateChangeEventTranslator eventTranslator, ValuesBuilderFactory valuesBuilderFactory, ObjectSerializer objectSerializer, ObjectMapper objectMapper, ObjectValidator objectValidator,
      @Lazy ReceivingSender receivingSender) {
    super(eventTranslator, valuesBuilderFactory, objectSerializer, objectMapper, objectValidator, receivingSender);
  }

  @Override
  public boolean validateStateType(StateChangeEvent serviceOrder) {
    Optional<StateChangeEvent> stateChangeEvent = Optional.ofNullable(serviceOrder).stream()
        .filter(event -> supportedState().equals(ServiceOrderStateType.fromValue(event.getState()))).findFirst();
    if(stateChangeEvent.isEmpty()){
      return false;
    }
    return stateChangeEvent.stream().noneMatch(e -> e.getPhaseNote() != null && e.getPhaseNote().getMessage() != null
        && e.getPhaseNote().getMessage().toLowerCase().contains("has entered in fallout"));
  }

  @Override
  public ServiceOrderStateType supportedState() {
    return ServiceOrderStateType.INPROGRESS;
  }

}
