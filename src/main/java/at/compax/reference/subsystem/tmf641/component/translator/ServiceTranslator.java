package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.Optional;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.ValueType;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.Service;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID, required = false)
@SubsystemPayload(value = ValueKeys.SERVICE_CHARACTERISTIC, required = false, type = ValueType.LIST_VALUE, translator = ServiceCharacteristicListTranslator.class)
@SubsystemPayload(value = ValueKeys.SERVICE_SPECIFICATION, required = false, type = ValueType.MAP_VALUE, translator = ServiceSpecificationTranslator.class)
public class ServiceTranslator implements Translator<Service> {

  private final ServiceCharacteristicListTranslator serviceCharacteristicListTranslator;
  private final ServiceSpecificationTranslator serviceSpecificationTranslator;

  @Override
  public Service translate(ValuesConsumer consumer) {
    Service service = new Service();
    service.setId(consumer.stringValueOptional(ValueKeys.ID));
    service.setServiceCharacteristic(serviceCharacteristicListTranslator.translate(consumer));
    service.setServiceSpecification(serviceSpecificationTranslator.translate(consumer.mapValue(ValueKeys.SERVICE_SPECIFICATION)));
    return service;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, Service model) {
    builder.addMapValue(ValueKeys.SERVICE) //
        .addValue(ValueKeys.ID, model.getId());
    Optional.ofNullable(model.getServiceCharacteristic()).ifPresent(serviceCharacteristic -> serviceCharacteristicListTranslator.translate(builder, serviceCharacteristic));
    Optional.ofNullable(model.getServiceSpecification()).ifPresent(serviceSpecification -> serviceSpecificationTranslator.translate(builder, serviceSpecification));
    return builder.previous();
  }

}
