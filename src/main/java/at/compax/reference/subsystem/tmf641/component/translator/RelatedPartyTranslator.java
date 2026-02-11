package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.RelatedParty;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID)
@SubsystemPayload(value = ValueKeys.HREF, required = false)
@SubsystemPayload(value = ValueKeys.ROLE, required = false)
@SubsystemPayload(value = ValueKeys.NAME, required = false)
@SubsystemPayload(value = ValueKeys.AT_BASE_TYPE, required = false)
@SubsystemPayload(value = ValueKeys.AT_SCHEMA_LOCATION, required = false)
@SubsystemPayload(value = ValueKeys.AT_TYPE, required = false)
@SubsystemPayload(value = ValueKeys.AT_REFERRED_TYPE, required = false)
public class RelatedPartyTranslator implements Translator<RelatedParty> {

  @Override
  public RelatedParty translate(ValuesConsumer consumer) {
    RelatedParty relatedParty = new RelatedParty();
    relatedParty.setId(consumer.stringValue(ValueKeys.ID));
    relatedParty.setName(consumer.stringValueOptional(ValueKeys.NAME));
    relatedParty.setRole(consumer.stringValueOptional(ValueKeys.ROLE));
    relatedParty.setAtBaseType(consumer.stringValueOptional(ValueKeys.AT_BASE_TYPE));
    relatedParty.setAtType(consumer.stringValueOptional(ValueKeys.AT_TYPE));
    relatedParty.setAtReferredType(consumer.stringValue(ValueKeys.AT_REFERRED_TYPE));
    return relatedParty;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, RelatedParty model) {
    builder.addMapValue() //
        .addValue(ValueKeys.ID, model.getId()) //
        .addValue(ValueKeys.NAME, model.getName()) //
        .addValue(ValueKeys.ROLE, model.getRole()) //
        .addValue(ValueKeys.AT_BASE_TYPE, model.getAtBaseType()) //
        .addValue(ValueKeys.AT_TYPE, model.getAtType()) //
        .addValue(ValueKeys.AT_REFERRED_TYPE, model.getAtReferredType());
    return builder.previous();
  }

}
