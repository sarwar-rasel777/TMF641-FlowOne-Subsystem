package at.compax.reference.subsystem.tmf641.component.translator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.compax.foundation.subsystem.api.component.translator.v2.IncomingTranslator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.Event;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderCreateEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventCreateEventTranslator implements IncomingTranslator<Event> {

  private final ObjectMapper objectMapper;
  private final CreateEventTranslator createEventTranslator;

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, Event model) {
    ServiceOrderCreateEventPayload serviceOrderCreateEventPayload = objectMapper.convertValue(model.getEvent(), ServiceOrderCreateEventPayload.class);
    builder.addValue(ValueKeys.EXTERNAL_ID, Objects.requireNonNull(serviceOrderCreateEventPayload.getServiceOrder()).getExternalId());
    builder.addMapValue(ValueKeys.SERVICE_ORDER);
    createEventTranslator.translate(builder, serviceOrderCreateEventPayload.getServiceOrder());
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00");
    // required because the builder only accepts strings
    String eventTimeString = simpleDateFormat.format(Date.from(Objects.requireNonNull(model.getEventTime()).toInstant()));

    builder.previous();
    builder.addValue(ValueKeys.EVENT_ID, model.getEventId()) //
        .addValue(ValueKeys.EVENT_TIME, eventTimeString) //
        .addValue(ValueKeys.EVENT_TYPE, model.getEventType());
    return builder;
  }

}
