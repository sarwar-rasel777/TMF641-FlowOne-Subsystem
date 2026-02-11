package at.compax.reference.subsystem.tmf641.component.service;

import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadReceivingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.service.component.model.ValuesBuilderFactory;
import at.compax.foundation.subsystem.service.component.service.ReceivingSender;
import at.compax.foundation.subsystem.service.component.service.ReceivingService;
import at.compax.foundation.subsystem.service.component.service.SubsystemService;
import at.compax.reference.subsystem.tmf641.component.exceptions.Tmf641FlowOneException;
import at.compax.reference.subsystem.tmf641.component.model.CreateCancelEvent;
import at.compax.reference.subsystem.tmf641.component.model.Event;
import at.compax.reference.subsystem.tmf641.component.model.ObjectSerializer;
import at.compax.reference.subsystem.tmf641.component.model.ObjectValidator;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderCancelCreateEvent;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItem;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderStateType;
import at.compax.reference.subsystem.tmf641.component.translator.EventCreateCancelEventTranslator;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SubsystemPayload(translator = EventCreateCancelEventTranslator.class)
public abstract class ServiceOrderCancelCreateEventService implements SubsystemService, ReceivingService<Event>, ServiceOrderStateTypeValidator {

  public static final String STATE_CHANGE_EVENT_TYPE = "ServiceOrderStateChangeEvent";

  private final EventCreateCancelEventTranslator eventTranslator;
  private final ValuesBuilderFactory valuesBuilderFactory;
  private final ObjectSerializer objectSerializer;
  private final ObjectMapper objectMapper;
  private final ObjectValidator objectValidator;
  private final ReceivingSender receivingSender;

  protected ServiceOrderCancelCreateEventService(EventCreateCancelEventTranslator eventTranslator, ValuesBuilderFactory valuesBuilderFactory, ObjectSerializer objectSerializer, ObjectMapper objectMapper, ObjectValidator objectValidator, ReceivingSender receivingSender) {
    this.eventTranslator = eventTranslator;
    this.valuesBuilderFactory = valuesBuilderFactory;
    this.objectSerializer = objectSerializer;
    this.objectMapper = objectMapper;
    this.objectValidator = objectValidator;
    this.receivingSender = receivingSender;
  }

  @Override
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    throw new UnsupportedOperationException("createRequest() not supported for " + model.getPayloadTypeName() + ". SubsystemService is for testing purposes only.");
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    Event event = objectValidator.readAndValidateObject(model, getRequestType());
    receivingSender.sendPayload(event);
    return new PayloadSendingResponseModel();
  }

  @Override
  public PayloadReceivingModel receivePayload(Event request, String payloadTypeName) {
    byte[] payload = objectSerializer.toBytes(request);
    ValuesBuilder builder = valuesBuilderFactory.builder();
    eventTranslator.translate(builder, request);
    return new PayloadReceivingModel().payloadTypeName(payloadTypeName).payload(payload).values(builder.toValues());
  }

  @Override
  public boolean support(Event event) {
    if (event == null || event.getEventType() == null) {
      log.warn("FlowOne ServiceOrderEvent {} is not valid", event);
      return false;
    }
    if (!STATE_CHANGE_EVENT_TYPE.equals(event.getEventType())) {
      return false;
    }
    CreateCancelEvent serviceOrder;
    try {
      ServiceOrderCancelCreateEvent serviceOrderCreateEvent = objectMapper.convertValue(event, ServiceOrderCancelCreateEvent.class);
      if (serviceOrderCreateEvent.getEvent() == null) {
        throw new Tmf641FlowOneException(HttpStatus.INTERNAL_SERVER_ERROR, "FlowOne ServiceOrderEvent doesn't contain a valid event");
      }
      serviceOrder = serviceOrderCreateEvent.getEvent().getServiceOrder();
      if (serviceOrder == null) {
        throw new Tmf641FlowOneException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("FlowOne ServiceOrderEvent %s doesn't contain a valid service order", event.getEventId()));
      }
      if (ServiceOrderStateType.fromValue(serviceOrder.getState()) == null) {
        throw new Tmf641FlowOneException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("FlowOne ServiceOrderEvent %s doesn't contain a valid state %s", event.getEventId(), serviceOrder.getState()));
      }
    } catch (RuntimeException e) {
      log.warn("FlowOne ServiceOrderEvent {} doesn't contain a valid ServiceOrder\n{}", event.getEventId(), e);
      return false;
    }

    List<ServiceOrderItem> serviceOrderItems = serviceOrder.getServiceOrderItem();
    if (CollectionUtils.isEmpty(serviceOrderItems)) {
      return false;
    }
    return validateStateType(serviceOrder);
  }

  private boolean validateStateType(CreateCancelEvent serviceOrder) {
    return Optional.ofNullable(serviceOrder) //
        .map(CreateCancelEvent::getState) //
        .map(ServiceOrderStateType::fromValue)
        .filter(supportedState()::equals) //
        .isPresent();
  }

  @Override
  public Class<Event> getRequestType() {
    return Event.class;
  }

}
