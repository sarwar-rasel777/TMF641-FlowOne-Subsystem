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
import at.compax.reference.subsystem.tmf641.component.model.ServiceCharacteristic;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.SERVICE_CHARACTERISTIC, type = ValueType.MAP_VALUE, translator = CharacteristicTranslator.class, required = false)
public class ServiceCharacteristicListTranslator implements Translator<List<ServiceCharacteristic>> {

  private final CharacteristicTranslator characteristicTranslator;

  @Override
  public List<ServiceCharacteristic> translate(ValuesConsumer consumer) {
    List<ServiceCharacteristic> characteristics = new ArrayList<>();
    consumer.listValue(ValueKeys.SERVICE_CHARACTERISTIC).forEach(singleValueConsumer -> characteristics.add(characteristicTranslator.translate(singleValueConsumer.mapValue())));
    return characteristics;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, List<ServiceCharacteristic> model) {
    builder.addListValue(ValueKeys.SERVICE_CHARACTERISTIC);
    model.forEach(characteristic -> characteristicTranslator.translate(builder, characteristic));
    return builder.previous();
  }

}
