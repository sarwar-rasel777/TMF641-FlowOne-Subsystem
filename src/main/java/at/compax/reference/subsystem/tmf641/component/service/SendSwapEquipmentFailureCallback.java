package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.compax.reference.subsystem.tmf641.component.translator.FailureCallbackTranslator;
import lombok.Getter;

@Getter
@Service("sendSwapEquipmentFailureCallback")
public class SendSwapEquipmentFailureCallback extends AbstractCallbackSender {

  @Autowired
  private FailureCallbackTranslator translator;

  @Override
  protected String getAction() {
    return "replace";
  }

}
