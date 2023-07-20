package com.sabu.schedulerquartzpoc.controller;

import com.sabu.schedulerquartzpoc.model.JobDescriptor;
import com.sabu.schedulerquartzpoc.model.TriggerDescriptor;
import com.sabu.schedulerquartzpoc.service.JobService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : sabu.shakya
 * @created : 2023-06-29
 **/
@RestController
@RequestMapping("/test/scheduler")
@RequiredArgsConstructor
public class TestController {

  private final JobService jobService;

  @PostMapping
  public ResponseEntity<?> triggerTestJob() {

    Map<String, Object> data = new HashMap<>();
    data.put("tKey", "tdata");
    JobDescriptor jobDescriptor = new JobDescriptor();
    jobDescriptor.setName("test");
    jobDescriptor.setGroup("test");
    jobDescriptor.setData(data);
    jobDescriptor.setType("com.sabu.schedulerquartzpoc.job.TestJob");
    jobDescriptor.setDescription("This is test");

    TriggerDescriptor triggerDescriptor = new TriggerDescriptor();
    triggerDescriptor.setFireTime(LocalDateTime.now().plusSeconds(30));

    jobDescriptor.setTriggerDescriptors(Collections.singletonList(triggerDescriptor));

    jobService.createJob(jobDescriptor);

    return ResponseEntity.noContent().build();
  }

  // APIS to update, replace, pause, resume, get and delete

}
