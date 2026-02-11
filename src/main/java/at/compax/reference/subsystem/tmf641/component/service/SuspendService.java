package at.compax.reference.subsystem.tmf641.component.service;

import org.springframework.stereotype.Service;

import at.compax.foundation.subsystem.api.model.v2.PayloadCreationModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadCreationResponseModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingModel;
import at.compax.foundation.subsystem.api.model.v2.PayloadSendingResponseModel;
import at.compax.reference.subsystem.tmf641.component.translator.ServiceCreateOrderRequestTranslator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service("suspendService")
public class SuspendService extends AbstractService {
  private final ServiceCreateOrderRequestTranslator serviceCreateOrderRequestTranslator;

  @Override
  @SneakyThrows
  public PayloadCreationResponseModel createRequest(PayloadCreationModel model) {
    return getPayloadCreationResponseModel(model, serviceCreateOrderRequestTranslator);
  }

  @Override
  public PayloadSendingResponseModel sendRequest(PayloadSendingModel model) {
    log.info("Sending suspendService request...");
    return getPayloadSendingResponseModel(model);
  }

}
