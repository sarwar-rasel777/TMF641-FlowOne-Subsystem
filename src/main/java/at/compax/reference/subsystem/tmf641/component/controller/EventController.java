package at.compax.reference.subsystem.tmf641.component.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.compax.foundation.subsystem.service.component.service.ReceivingSender;
import at.compax.foundation.subsystem.service.component.service.ReceivingServiceNotFoundException;
import at.compax.reference.subsystem.tmf641.component.model.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback/api/v1")
public class EventController {

  private final ReceivingSender receivingSender;

  @PostMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Notify BSS about a event", description = "Send event object to BSS", security = @SecurityRequirement(name = "oauth2"))
  public ResponseEntity<Void> eventCallback(@RequestBody @Valid Event event) {
    logPayload(event);
    try {
      receivingSender.sendPayload(event);
    } catch (ReceivingServiceNotFoundException e) {
      log.info("Event {} was not intended for TMF641 flowOne subsystem", event.getEventId());
    }
    return ResponseEntity.ok().build();
  }

  private void logPayload(Event event) {
    if (log.isDebugEnabled()) {
      log.debug("Request payload:\n{}", event.toString());
    }
  }

}
