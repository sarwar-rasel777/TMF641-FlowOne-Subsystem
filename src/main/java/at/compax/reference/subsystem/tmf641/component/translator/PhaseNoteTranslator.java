package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.ValueType;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.PhaseNote;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID, required = false)
@SubsystemPayload(value = ValueKeys.SERVICE_CHARACTERISTIC, required = false, type = ValueType.LIST_VALUE, translator = ServiceCharacteristicListTranslator.class)
@SubsystemPayload(value = ValueKeys.SERVICE_SPECIFICATION, required = false, type = ValueType.MAP_VALUE, translator = ServiceSpecificationTranslator.class)
public class PhaseNoteTranslator implements Translator<PhaseNote> {

  @Override
  public PhaseNote translate(ValuesConsumer consumer) {
    PhaseNote phaseNote = new PhaseNote();
    phaseNote.setId(consumer.stringValueOptional(ValueKeys.ID));
    phaseNote.setMessage(consumer.stringValueOptional(ValueKeys.MESSAGE));
    return phaseNote;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, PhaseNote model) {
    builder.addMapValue(ValueKeys.PHASE_NOTE) //
        .addValue(ValueKeys.ID, model.getId())
        .addValue(ValueKeys.MESSAGE, model.getMessage());
    return builder.previous();
  }

}
