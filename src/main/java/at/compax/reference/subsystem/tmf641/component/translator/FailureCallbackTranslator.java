package at.compax.reference.subsystem.tmf641.component.translator;

import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.model.InstallationCallbackModel;

@Component
public class FailureCallbackTranslator implements Translator<InstallationCallbackModel> {

  @Override
  public InstallationCallbackModel translate(ValuesConsumer consumer) {
    InstallationCallbackModel model = new InstallationCallbackModel();
    model.setRequestId(consumer.stringValueOptional("workflowId"));
    model.setServiceId(consumer.stringValueOptional("uuId"));
    model.setFailureReason(consumer.stringValue("reasoncode"));
    model.setStatus("FAILURE");
    return model;
  }

}
