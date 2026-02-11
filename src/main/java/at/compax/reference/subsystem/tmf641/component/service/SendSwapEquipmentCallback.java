package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.compax.reference.subsystem.tmf641.component.translator.InstallationCallbackTranslator;
import lombok.Getter;

@Getter
@Service("sendSwapEquipmentCallback")
public class SendSwapEquipmentCallback extends AbstractCallbackSender {

  @Autowired
  private InstallationCallbackTranslator translator;

  @Override
  protected String getAction() {
    return "replace";
  }

}
