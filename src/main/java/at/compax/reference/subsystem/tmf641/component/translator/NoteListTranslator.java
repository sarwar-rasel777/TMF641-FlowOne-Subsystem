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
import at.compax.reference.subsystem.tmf641.component.model.Note;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SubsystemPayload(value = ValueKeys.NOTE, type = ValueType.MAP_VALUE, translator = NoteTranslator.class, required = false)
public class NoteListTranslator implements Translator<List<Note>> {

  private final NoteTranslator noteTranslator;

  @Override
  public List<Note> translate(ValuesConsumer consumer) {
    List<Note> note = new ArrayList<>();
    consumer.listValue(ValueKeys.NOTE).forEach(singleValueConsumer -> note.add(noteTranslator.translate(singleValueConsumer.mapValue())));
    return note;

  }

  @Override
  public ValuesBuilder translate(ValuesBuilder builder, List<Note> model) {
    builder.addListValue(ValueKeys.NOTE);
    model.forEach(note -> noteTranslator.translate(builder, note));
    return builder.previous();
  }

}
