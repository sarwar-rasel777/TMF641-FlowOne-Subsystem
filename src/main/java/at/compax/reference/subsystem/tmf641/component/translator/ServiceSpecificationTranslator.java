package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ServiceSpecification;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID)
@SubsystemPayload(value = ValueKeys.HREF, required = false)
@SubsystemPayload(value = ValueKeys.NAME, required = false)
@SubsystemPayload(value = ValueKeys.VERSION, required = false)
@SubsystemPayload(value = ValueKeys.AT_BASE_TYPE, required = false)
@SubsystemPayload(value = ValueKeys.AT_SCHEMA_LOCATION, required = false)
@SubsystemPayload(value = ValueKeys.AT_TYPE, required = false)
@SubsystemPayload(value = ValueKeys.AT_REFERRED_TYPE, required = false)
public class ServiceSpecificationTranslator implements Translator<ServiceSpecification> {

  @Override
  public ServiceSpecification translate(ValuesConsumer consumer) {
    ServiceSpecification serviceSpecification = new ServiceSpecification();
    serviceSpecification.setId(consumer.stringValue(ValueKeys.ID));
    serviceSpecification.setName(consumer.stringValueOptional(ValueKeys.NAME));
    serviceSpecification.setVersion(consumer.stringValueOptional(ValueKeys.VERSION));
    return serviceSpecification;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, ServiceSpecification model) {
    builder.addMapValue(ValueKeys.SERVICE_SPECIFICATION) //
        .addValue(ValueKeys.ID, model.getId()) //
        .addValue(ValueKeys.NAME, model.getName()) //
        .addValue(ValueKeys.VERSION, model.getVersion());
    return builder.previous();
  }

}
