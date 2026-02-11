package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.CancelServiceOrder;

@Component
public class InFlightCancelOrderRequestTranslator implements Translator<CancelServiceOrder> {

  @Override
  public CancelServiceOrder translate(ValuesConsumer consumer) {
    CancelServiceOrder request = new CancelServiceOrder();

    request.setId(consumer.stringValue(ValueKeys.FLOWONE_ID));
    request.setCancellationReason(consumer.stringValueOptional(ValueKeys.CANCELLATION_REASON));
    request.setRequestedCancellationDate(String.valueOf(consumer.dateTimeValueOffsetOptional("requestedDate")));

    return request;
  }

}
