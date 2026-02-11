package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.RegisterListener;

@Component
public class HubRequestTranslator implements Translator<RegisterListener> {
  @Override
  public RegisterListener translate(ValuesConsumer consumer) {
    RegisterListener request = new RegisterListener();
    request.setId(consumer.integerValue(ValueKeys.ID));
    request.setCallback(consumer.stringValueOptional(ValueKeys.CALL_BACK));
    request.setIsAuthEnabled(consumer.stringValueOptional(ValueKeys.IS_AUTH_ENABLED));
    request.setAuthUsername(consumer.stringValueOptional(ValueKeys.AUTH_USER_NAME));
    request.setAuthPassword(consumer.stringValueOptional(ValueKeys.AUTH_PASSWORD));
    return request;
  }

}
