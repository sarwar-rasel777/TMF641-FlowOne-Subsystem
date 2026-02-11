package at.compax.reference.subsystem.tmf641.component.simulator.controller;

import java.util.UUID;

public class TMF641SimulationResponseGenerator {

  public static String getOntStatusResponse(Long ontStatus) {
    if (translatesToErrorExpected(ontStatus)) {
      throw new RuntimeException("An error response has been configured, so here it is");
    }
    return "{\n"
        + "    \"eventId\": \"" + UUID.randomUUID() + "\",\n"
        + "    \"eventTime\": \"2023-11-16T15:22:12.459Z\",\n"
        + "    \"eventType\": \"ServiceOrderStateChangeEvent\",\n"
        + "    \"event\": {\n"
        + "        \"serviceOrder\": {\n"
        + "            \"externalId\": \"" + UUID.randomUUID() + "\",\n"
        + "            \"id\": 1370,\n"
        + "            \"state\": \"" + (translatesToNotFound(ontStatus) ? "failed" : "completed") + "\",\n"
        + "            \"stateMessage\": \"Order delivered\",\n"
        + "            \"priority\": 5,\n"
        + "            \"href\": null,\n"
        + "            \"category\": \"HSI_GetONTStatus\",\n"
        + "            \"description\": \"HSI GetONTStatus\",\n"
        + "            \"startDate\": \"2023-11-16T15:22:07.053Z\",\n"
        + "            \"orderDate\": \"2023-11-16T15:22:07.053Z\",\n"
        + "            \"completionDate\": \"2023-11-16T15:22:12.400Z\",\n"
        + "            \"requestedCompletionDate\": null,\n"
        + "            \"notificationContact\": null,\n"
        + "            \"@baseType\": null,\n"
        + "            \"@schemaLocation\": null,\n"
        + "            \"@type\": \"GETONTSTATUS\",\n"
        + "            \"relatedParty\": [\n"
        + "                {\n"
        + "                    \"id\": \"some_accountId\",\n"
        + "                    \"role\": \"customer\"\n"
        + "                }\n"
        + "            ],\n"
        + "            \"serviceOrderItem\": [\n"
        + "                {\n"
        + "                    \"id\": 1,\n"
        + "                    \"action\": \"GETONTSTATUS\",\n"
        + "                    \"@type\": \"GETONTSTATUS\""
        + (translatesToNotFound(ontStatus) ? "\n" : (",\n"
        + "                    \"note\": [\n"
        + "                        {\n"
        + "                            \"id\": \"OPER_STATUS\",\n"
        + "                            \"text\": \"" + (translatesToOntDown(ontStatus) ? "down" : "up") + "\"\n"
        + "                        },\n"
        + "                        {\n"
        + "                            \"id\": \"ADMIN_STATUS\",\n"
        + "                            \"text\": \"" + (translatesToOntDown(ontStatus) ? "down" : "up") + "\"\n"
        + "                        }\n"
        + "                    ]\n"))
        + "                }\n"
        + "            ]\n"
        + "        }\n"
        + "    }\n"
        + "}";
  }

  private static boolean translatesToNotFound(Long ontStatus) {
    return ontStatus.equals(0L);
  }

  private static boolean translatesToOntDown(Long ontStatus) {
    return ontStatus.equals(2L);
  }

  private static boolean translatesToErrorExpected(Long ontStatus) {
    return ontStatus.equals(99L);
  }

}
