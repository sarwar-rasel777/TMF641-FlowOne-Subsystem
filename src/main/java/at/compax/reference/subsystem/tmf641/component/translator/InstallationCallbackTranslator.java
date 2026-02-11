package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ServiceCharacteristicNames;
import at.compax.reference.subsystem.tmf641.component.model.InstallationCallbackModel;
import at.compax.reference.subsystem.tmf641.component.model.InstallationCharacteristics;

@Component
public class InstallationCallbackTranslator implements Translator<InstallationCallbackModel> {

  @Override
  public InstallationCallbackModel translate(ValuesConsumer consumer) {
    InstallationCallbackModel model = new InstallationCallbackModel();
    model.setRequestId(consumer.stringValueOptional("workflowId"));
    model.setServiceId(consumer.stringValueOptional("uuid"));
    model.setStatus("SUCCESS");
    
    List<InstallationCharacteristics> installationCharacteristics = new ArrayList<>();
    String ontSerialNumber = consumer.stringValueOptional("ontSerialNumber");
    if (!StringUtils.isBlank(ontSerialNumber)) {
      InstallationCharacteristics ontCharacteristics = new InstallationCharacteristics();
      ontCharacteristics.setKey(ServiceCharacteristicNames.ONT_ID);
      ontCharacteristics.setValue(ontSerialNumber);
      installationCharacteristics.add(ontCharacteristics);
    }
    String macAddress = consumer.stringValueOptional("macAddress");
    if (!StringUtils.isBlank(macAddress)) {
      InstallationCharacteristics characteristicMac = new InstallationCharacteristics();
      characteristicMac.setKey(ServiceCharacteristicNames.MAC_ADDRESS);
      characteristicMac.setValue(macAddress);
      installationCharacteristics.add(characteristicMac);
    }

    model.setInstallationCharacteristics(installationCharacteristics);
    return model;
  }

}
