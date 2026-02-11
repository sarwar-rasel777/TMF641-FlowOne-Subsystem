package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.List;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.component.translator.v2.Translator;
import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ServiceCharacteristicNames;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.RelatedParty;
import at.compax.reference.subsystem.tmf641.component.model.Service;
import at.compax.reference.subsystem.tmf641.component.model.ServiceCharacteristic;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItem;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderPatch;

@Component
public class InFlightOrderAmendmentRequestTranslator implements Translator<ServiceOrderPatch> {

  @Override
  public ServiceOrderPatch translate(ValuesConsumer consumer) {
    ServiceOrderPatch request = new ServiceOrderPatch();
    request.setCategory(consumer.stringValue(ValueKeys.CATEGORY));
    request.setDescription(consumer.stringValue(ValueKeys.DESCRIPTION));
    request.setAtType(consumer.stringValueOptional(ValueKeys.AT_TYPE));
    List<RelatedParty> relatedPartyList = List.of(newRelatedParty(consumer.stringValue(ValueKeys.ACCOUNT_ID), consumer.stringValue(ValueKeys.ROLE)));
    request.setRelatedParty(relatedPartyList);

    ServiceOrderItem serviceOrderItem = new ServiceOrderItem();
    serviceOrderItem.setId("1");
    serviceOrderItem.setAtType(consumer.stringValue(ValueKeys.AT_TYPE));

    Service service = new Service();
    serviceOrderItem.setService(service);
    List<ServiceCharacteristic> serviceCharacteristics = service.getServiceCharacteristic();
    serviceCharacteristics.add(newServiceCharacteristic(ServiceCharacteristicNames.MBIT_UP, consumer.stringValue(ValueKeys.MBIT_UP_NEW)));
    serviceCharacteristics.add(newServiceCharacteristic(ServiceCharacteristicNames.MBIT_DOWN, consumer.stringValue(ValueKeys.MBIT_DOWN_NEW)));
    serviceCharacteristics.add(newServiceCharacteristic(ServiceCharacteristicNames.ADDRESS_KEY, consumer.stringValue(ValueKeys.ADDRESS_KEY)));
    request.getServiceOrderItem().add(serviceOrderItem);

    return request;
  }

  protected RelatedParty newRelatedParty(String id, String role) {
    RelatedParty relatedParty = new RelatedParty();
    relatedParty.setId(id);
    relatedParty.setRole(role);
    return relatedParty;
  }

  private ServiceCharacteristic newServiceCharacteristic(String name, String value) {
    ServiceCharacteristic serviceCharacteristic = new ServiceCharacteristic();
    serviceCharacteristic.setName(name);
    serviceCharacteristic.setValue(value);
    return serviceCharacteristic;
  }

}
