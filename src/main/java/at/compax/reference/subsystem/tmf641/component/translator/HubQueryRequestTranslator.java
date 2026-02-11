package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ListenerRequest;

@Component
public class HubQueryRequestTranslator implements Translator<ListenerRequest> {

  @Override
  public ListenerRequest translate(ValuesConsumer consumer) {
    ListenerRequest request = new ListenerRequest();
    request.setId(consumer.integerValue(ValueKeys.ID));
    return request;
  }

}
