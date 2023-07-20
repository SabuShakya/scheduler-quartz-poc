package com.sabu.schedulerquartzpoc.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author : sabu.shakya
 * @created : 2023-07-06
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmail extends AbstractJob {

  @Value("${spring.mail.username}")
  private String email;

  private final JavaMailSender javaMailSender;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    this.log(context);

    JobDataMap jobDataMap = this.getJobDataMap(context);

    this.sendEmail(
        (String) jobDataMap.get("toEmail"),
        (String) jobDataMap.get("subject"),
        (String) jobDataMap.get("message"));
  }

  public void sendEmail(String toEmail, String subject, String message) {
    log.info("Preparing to send email to : {} ", toEmail);

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(toEmail);
    mailMessage.setSubject(subject);
    mailMessage.setText(message);
    mailMessage.setFrom(email);

    javaMailSender.send(mailMessage);

    log.info("Completed sending email.");
  }
}
