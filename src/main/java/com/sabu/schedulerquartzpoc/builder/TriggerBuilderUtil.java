package com.sabu.schedulerquartzpoc.builder;

import static java.util.UUID.randomUUID;

import com.sabu.schedulerquartzpoc.model.JobDescriptor;
import com.sabu.schedulerquartzpoc.model.TriggerDescriptor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

/**
 * @author : sabu.shakya
 * @created : 2023-06-27
 **/
@Slf4j
@Component
public class TriggerBuilderUtil {

  public Set<Trigger> buildTriggers(JobDescriptor jobDescriptor) {
    return jobDescriptor.getTriggerDescriptors()
        .stream()
        .map(triggerDescriptor -> buildTrigger(jobDescriptor, triggerDescriptor))
        .collect(Collectors.toSet());

  }

  public Trigger buildTrigger(JobDescriptor descriptor, TriggerDescriptor triggerDescriptor) {
    String cron = triggerDescriptor.getCron();
    LocalDateTime fireTime = triggerDescriptor.getFireTime();

    if (Objects.nonNull(cron) && !cron.isEmpty()) {
      return buildCronTrigger(descriptor, cron);

    } else if (Objects.nonNull(fireTime) && fireTime.isAfter(LocalDateTime.now())) {
      return buildSimpleTrigger(descriptor, fireTime);
    }

    // CREATE APPROPRIATE EXCEPTION
    throw new RuntimeException("Unsupported trigger descriptor");
  }

  private SimpleTrigger buildSimpleTrigger(JobDescriptor descriptor, LocalDateTime fireTime) {
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("fireTime", fireTime);
    jobDataMap.putAll(descriptor.getData());

    return org.quartz.TriggerBuilder
        .newTrigger()
        .withIdentity(buildName(descriptor.getName()), descriptor.getGroup())
        .withSchedule(
            SimpleScheduleBuilder
                .simpleSchedule()
                .withMisfireHandlingInstructionNextWithExistingCount())
        .startAt(Date.from(fireTime.atZone(ZoneId.systemDefault()).toInstant()))
        .usingJobData(jobDataMap)
        .build();
  }

  private CronTrigger buildCronTrigger(JobDescriptor descriptor, String cron) {
    if (!CronExpression.isValidExpression(cron)) {
      throw new IllegalArgumentException(
          "Provided expression " + cron + " is not a valid cron expression");
    }

    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("cron", cron);
    jobDataMap.putAll(descriptor.getData());

    return org.quartz.TriggerBuilder
        .newTrigger()
        .withIdentity(buildName(descriptor.getName()), descriptor.getGroup())
        .withSchedule(
            CronScheduleBuilder
                .cronSchedule(cron)
                .withMisfireHandlingInstructionFireAndProceed()
                .inTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault())))
        .usingJobData(jobDataMap)
        .build();
  }

  private String buildName(String name) {
    return name.isEmpty() ? randomUUID().toString() : name;
  }

  public static TriggerDescriptor buildDescriptor(Trigger trigger) {
    return TriggerDescriptor
        .builder()
        .fireTime((LocalDateTime) trigger.getJobDataMap().get("fireTime"))
        .cron(trigger.getJobDataMap().getString("cron"))
        .build();
  }

}
