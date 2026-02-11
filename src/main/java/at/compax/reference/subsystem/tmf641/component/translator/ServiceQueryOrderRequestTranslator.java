package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderCreate;

@Component
public class ServiceQueryOrderRequestTranslator implements Translator<ServiceOrderCreate> {

  @Override
  public ServiceOrderCreate translate(ValuesConsumer consumer) {
    ServiceOrderCreate request = new ServiceOrderCreate();
    request.setExternalId(consumer.stringValue(ValueKeys.ID));
    return request;
  }

}
