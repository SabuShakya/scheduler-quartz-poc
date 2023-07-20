package com.sabu.schedulerquartzpoc.service;

import static com.sabu.schedulerquartzpoc.constants.JobConstants.EMAIL_JOB;

import com.sabu.schedulerquartzpoc.model.JobDescriptor;
import com.sabu.schedulerquartzpoc.model.MailRequest;
import com.sabu.schedulerquartzpoc.model.TriggerDescriptor;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author : sabu.shakya
 * @created : 2023-07-20
 **/
@Service
@RequiredArgsConstructor
public class MailService {

  private final JobService jobService;

  public void sendEmail(MailRequest mailRequest) {
    Map<String, Object> data = buildData(mailRequest);

    JobDescriptor jobDescriptor = buildJobDescriptor(data);

    jobService.createJob(jobDescriptor);
  }

  private static JobDescriptor buildJobDescriptor(Map<String, Object> data) {
    JobDescriptor jobDescriptor = new JobDescriptor();

    jobDescriptor.setGroup("email");
    // NEEDS TO BE UNIQUE
    jobDescriptor.setName("mailTo:"+ data.get("toEmail")+ UUID.randomUUID());
    jobDescriptor.setData(data);
    jobDescriptor.setType(EMAIL_JOB);
    jobDescriptor.setDescription("Schedule send email.");

    TriggerDescriptor triggerDescriptor = buildTrigger();

    jobDescriptor.setTriggerDescriptors(Collections.singletonList(triggerDescriptor));

    return jobDescriptor;
  }

  private static TriggerDescriptor buildTrigger() {
    TriggerDescriptor triggerDescriptor = new TriggerDescriptor();
    triggerDescriptor.setFireTime(LocalDateTime.now().plusSeconds(30));
    return triggerDescriptor;
  }

  private static Map<String, Object> buildData(MailRequest mailRequest) {
    Map<String, Object> data = new HashMap<>();

    data.put("toEmail", mailRequest.getToEmail());
    data.put("subject", mailRequest.getSubject());
    data.put("message", mailRequest.getMessage());
    return data;
  }

}
