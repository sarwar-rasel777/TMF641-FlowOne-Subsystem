package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.Optional;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.IncomingTranslator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.StateChangeEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID)
public class StateChangeEventTranslator implements IncomingTranslator<StateChangeEvent> {

  private final RelatedPartyListTranslator relatedPartyListTranslator;
  private final ServiceOrderItemStateChangeEventListTranslator serviceOrderItemStateChangeEventListTranslator;
  private final PhaseNoteTranslator phaseNoteTranslator;
  private final NoteListTranslator noteListTranslator;

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, StateChangeEvent model) {
    builder.addValue(ValueKeys.ID, model.getId())
        .addValue(ValueKeys.STATE, model.getState())
        .addValue(ValueKeys.ORDER_DATE, model.getOrderDate())
        .addValue(ValueKeys.START_DATE, model.getStartDate())
        .addValue(ValueKeys.COMPLETION_DATE, model.getCompletionDate())
        .addValue(ValueKeys.EXTERNAL_ID, model.getExternalId())
        .addValue(ValueKeys.AT_TYPE, model.getAtType())
        .addValue(ValueKeys.CATEGORY, model.getCategory())
        .addValue(ValueKeys.DESCRIPTION, model.getDescription())
        .addValue(ValueKeys.PRIORITY, model.getPriority());
    Optional.ofNullable(model.getRelatedParty()).ifPresent(relatedParty -> relatedPartyListTranslator.translate(builder, relatedParty));
    Optional.ofNullable(model.getServiceOrderItem()).ifPresent(serviceOrderItem -> serviceOrderItemStateChangeEventListTranslator.translate(builder, serviceOrderItem));
    Optional.ofNullable(model.getPhaseNote()).ifPresent(phaseNote -> phaseNoteTranslator.translate(builder, phaseNote));
    Optional.ofNullable(model.getNote()).ifPresent(note -> noteListTranslator.translate(builder, note));
    return builder;
  }

}
