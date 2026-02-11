package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ServiceCharacteristic;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID, required = false)
@SubsystemPayload(value = ValueKeys.NAME)
@SubsystemPayload(value = ValueKeys.VALUE_TYPE, required = false)
@SubsystemPayload(value = ValueKeys.VALUE)
public class CharacteristicTranslator implements Translator<ServiceCharacteristic> {

  @Override
  public ServiceCharacteristic translate(ValuesConsumer consumer) {
    ServiceCharacteristic characteristic = new ServiceCharacteristic();
    characteristic.setId(consumer.stringValueOptional(ValueKeys.ID));
    characteristic.setName(consumer.stringValue(ValueKeys.NAME));
    characteristic.setValueType(consumer.stringValueOptional(ValueKeys.VALUE_TYPE));
    characteristic.setValue(consumer.stringValue(ValueKeys.VALUE));
    return characteristic;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, ServiceCharacteristic model) {
    return builder.addMapValue() //
        .addValue(ValueKeys.ID, model.getId()) //
        .addValue(ValueKeys.NAME, model.getName()) //
        .addValue(ValueKeys.VALUE_TYPE, model.getValueType()) //
        .addValue(ValueKeys.VALUE, model.getValue()) //
        .previous();
  }

}
