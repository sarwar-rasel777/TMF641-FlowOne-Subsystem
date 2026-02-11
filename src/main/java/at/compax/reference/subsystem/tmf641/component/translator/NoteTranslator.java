package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.annotation.SubsystemPayload;
import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesBuilder;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.Note;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.ID)
@SubsystemPayload(value = ValueKeys.TEXT, required = false)
@SubsystemPayload(value = ValueKeys.PARENT_ID, required = false)
public class NoteTranslator implements Translator<Note> {

  @Override
  public Note translate(ValuesConsumer consumer) {
    Note note = new Note();
    note.setId(consumer.stringValueOptional(ValueKeys.ID));
    note.setText(consumer.stringValueOptional(ValueKeys.TEXT));
    note.setParentid(consumer.stringValueOptional(ValueKeys.PARENT_ID));
    return note;
  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, Note model) {
    builder.addMapValue()
        .addValue(ValueKeys.ID, model.getId())
        .addValue(ValueKeys.TEXT, model.getText())
        .addValue(ValueKeys.PARENT_ID, model.getParentid());
    return builder.previous();
  }

}
