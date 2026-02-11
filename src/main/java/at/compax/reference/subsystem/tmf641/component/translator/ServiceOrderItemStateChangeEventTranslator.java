package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.Optional;
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
@SubsystemPayload(value = ValueKeys.ID)
@SubsystemPayload(value = ValueKeys.ACTION)
@SubsystemPayload(value = ValueKeys.AT_TYPE)
@SubsystemPayload(value = ValueKeys.SERVICE, type = ValueType.MAP_VALUE, translator = ServiceTranslator.class)
public class ServiceOrderItemStateChangeEventTranslator implements Translator<ServiceOrderItemStateChangeEvent> {

  private final ServiceTranslator serviceTranslator;

  @Override
  public ServiceOrderItemStateChangeEvent translate(ValuesConsumer consumer) {
    ServiceOrderItemStateChangeEvent serviceOrderItem = new ServiceOrderItemStateChangeEvent();
    serviceOrderItem.setId(consumer.stringValue(ValueKeys.ID));
    serviceOrderItem.setAction(consumer.stringValue(ValueKeys.ACTION));
    serviceOrderItem.setAtType(consumer.stringValue(ValueKeys.AT_TYPE));
    serviceOrderItem.setService(serviceTranslator.translate(consumer.mapValue(ValueKeys.SERVICE)));
    return serviceOrderItem;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, ServiceOrderItemStateChangeEvent model) {
    builder.addMapValue() //
        .addValue(ValueKeys.ID, model.getId()) //
        .addValue(ValueKeys.ACTION, model.getAction()) //
        .addValue(ValueKeys.AT_TYPE, model.getAtType());
    Optional.ofNullable(model.getService()).ifPresent(service -> serviceTranslator.translate(builder, service));
    return builder.previous();
  }

}
