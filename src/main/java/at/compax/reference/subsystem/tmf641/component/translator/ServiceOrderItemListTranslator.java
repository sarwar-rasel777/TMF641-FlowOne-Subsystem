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
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItem;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.SERVICE_ORDER_ITEM, type = ValueType.MAP_VALUE, translator = ServiceOrderItemTranslator.class)
public class ServiceOrderItemListTranslator implements Translator<List<ServiceOrderItem>> {

  private final ServiceOrderItemTranslator serviceOrderItemTranslator;

  @Override
  public List<ServiceOrderItem> translate(ValuesConsumer consumer) {
    List<ServiceOrderItem> serviceOrderItems = new ArrayList<>();
    consumer.listValue(ValueKeys.SERVICE_ORDER_ITEM).forEach(singleValueConsumer -> serviceOrderItems.add(serviceOrderItemTranslator.translate(singleValueConsumer.mapValue())));
    return serviceOrderItems;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, List<ServiceOrderItem> model) {
    builder.addListValue(ValueKeys.SERVICE_ORDER_ITEM);
    model.forEach(serviceOrderItem -> serviceOrderItemTranslator.translate(builder, serviceOrderItem));
    return builder.previous();
  }

}
