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
import at.compax.reference.subsystem.tmf641.component.model.RelatedParty;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.RELATED_PARTY, type = ValueType.MAP_VALUE, translator = RelatedPartyTranslator.class, required = false)
public class RelatedPartyListTranslator implements Translator<List<RelatedParty>> {

  private final RelatedPartyTranslator relatedPartyTranslator;

  @Override
  public List<RelatedParty> translate(ValuesConsumer consumer) {
    List<RelatedParty> relatedParties = new ArrayList<>();
    consumer.listValue(ValueKeys.RELATED_PARTY).forEach(singleValueConsumer -> relatedParties.add(relatedPartyTranslator.translate(singleValueConsumer.mapValue())));
    return relatedParties;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, List<RelatedParty> model) {
    builder.addListValue(ValueKeys.RELATED_PARTY);
    model.forEach(relatedParty -> relatedPartyTranslator.translate(builder, relatedParty));
    return builder.previous();
  }

}
