package at.compax.reference.subsystem.tmf641.component.translator;

import java.util.List;
import org.springframework.stereotype.Component;

import at.compax.foundation.subsystem.api.model.v2.ValuesConsumer;
import at.compax.reference.subsystem.tmf641.component.constants.ServiceCharacteristicNames;
import at.compax.reference.subsystem.tmf641.component.constants.ValueKeys;
import at.compax.reference.subsystem.tmf641.component.model.Service;
import at.compax.reference.subsystem.tmf641.component.model.ServiceCharacteristic;
import at.compax.reference.subsystem.tmf641.component.model.ServiceOrderItem;
import at.compax.reference.subsystem.tmf641.component.model.ServiceSpecification;

@Component
public class ResetRouterRequestTranslator extends ServiceCreateOrderRequestTranslator {

  protected void addServiceToServiceOrderItem(ServiceOrderItem serviceOrderItem, ValuesConsumer consumer) {
    Service service = new Service();
    List<ServiceCharacteristic> serviceCharacteristics = service.getServiceCharacteristic();
    addServiceCharacteristic(serviceCharacteristics, consumer, ValueKeys.ADDRESS_KEY, ServiceCharacteristicNames.ADDRESS_KEY);
    addServiceCharacteristic(serviceCharacteristics, consumer, ValueKeys.MAC_ADDRESS, ServiceCharacteristicNames.MAC_ADDRESS);
    ServiceSpecification serviceSpecification = new ServiceSpecification();
    serviceSpecification.setId("1");
    serviceSpecification.setName("CFS_HSISERVICE"); // clarify, if this is a static value or should be part of product config
    serviceSpecification.setVersion("1.0");
    service.setServiceSpecification(serviceSpecification);
    serviceOrderItem.setService(service);
  }

}
