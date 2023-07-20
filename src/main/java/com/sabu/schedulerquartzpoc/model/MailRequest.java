package com.sabu.schedulerquartzpoc.model;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : sabu.shakya
 * @created : 2023-07-20
 **/
@Getter
@Setter
public class MailRequest {

  @NotBlank
  private String toEmail;

  @NotBlank
  private String subject;

  @NotBlank
  private String message;

}
