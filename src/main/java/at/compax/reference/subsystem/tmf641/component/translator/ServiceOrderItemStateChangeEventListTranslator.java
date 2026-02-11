package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.ValueType;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItemStateChangeEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.SERVICE_ORDER_ITEM, type = ValueType.MAP_VALUE, translator = ServiceOrderItemStateChangeEventTranslator.class)
public class ServiceOrderItemStateChangeEventListTranslator implements Translator<List<ServiceOrderItemStateChangeEvent>> {

  private final ServiceOrderItemStateChangeEventTranslator serviceOrderItemStateChangeEventTranslator;

  @Override
  public List<ServiceOrderItemStateChangeEvent> translate(ValuesConsumer consumer) {
    List<ServiceOrderItemStateChangeEvent> serviceOrderItems = new ArrayList<>();
    consumer.listValue(ValueKeys.SERVICE_ORDER_ITEM).forEach(singleValueConsumer -> serviceOrderItems.add(serviceOrderItemStateChangeEventTranslator.translate(singleValueConsumer.mapValue())));
    return serviceOrderItems;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, List<ServiceOrderItemStateChangeEvent> model) {
    builder.addListValue(ValueKeys.SERVICE_ORDER_ITEM);
    model.forEach(serviceOrderItemStateChangeEvent -> serviceOrderItemStateChangeEventTranslator.translate(builder, serviceOrderItemStateChangeEvent));
    return builder.previous();
  }

}
