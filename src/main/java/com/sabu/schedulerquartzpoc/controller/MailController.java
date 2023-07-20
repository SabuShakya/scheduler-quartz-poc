package com.sabu.schedulerquartzpoc.controller;

import com.sabu.schedulerquartzpoc.model.MailRequest;
import com.sabu.schedulerquartzpoc.service.MailService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sabu.shakya
 * @created : 2023-06-29
 **/
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class MailController {

  private final MailService mailService;

  @PostMapping("/mail")
  public ResponseEntity<?> triggerEmailJob(@RequestBody @Valid MailRequest mailRequest) {
    mailService.sendEmail(mailRequest);
    return ResponseEntity.noContent().build();
  }

}
