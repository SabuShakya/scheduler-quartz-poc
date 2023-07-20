package com.sabu.schedulerquartzpoc.service;

import static org.quartz.JobKey.jobKey;

import com.sabu.schedulerquartzpoc.builder.JobBuilderUtil;
import com.sabu.schedulerquartzpoc.builder.TriggerBuilderUtil;
import com.sabu.schedulerquartzpoc.model.JobDescriptor;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

/**
 * @author : sabu.shakya
 * @created : 2023-06-28
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

  private final Scheduler scheduler;

  private final JobBuilderUtil jobBuilder;

  private final TriggerBuilderUtil triggerBuilder;

  /**
   * @param jobDescriptor CREATES JOB BASED ON JOB DESCRIPTOR AND SCHEDULES AS PER THE TRIGGER
   *     DESCRIPTION
   */
  public void createJob(JobDescriptor jobDescriptor) {

    JobDetail jobDetail = jobBuilder.buildJobDetail(jobDescriptor);

    Set<Trigger> triggers = triggerBuilder.buildTriggers(jobDescriptor);

    try {
      log.info("Scheduling job with key :  {}", jobDetail.getKey());

      scheduler.scheduleJob(jobDetail, triggers, Boolean.FALSE);

      log.info("Job with  key : {} scheduled ", jobDetail.getKey());

    } catch (SchedulerException e) {
      log.error("Could not schedule job with key : {}, error:{}", jobDetail.getKey(),
          e.getLocalizedMessage(), e);
      throw new IllegalArgumentException(e.getLocalizedMessage());
    }

  }


  /**
   * @param jobDescriptor
   * IF NEED TO RE-USE THE JOB WITH SAME NAME (GROUP.NAME) BUT DIFFERENT JOB DATA
   */
  public void createAndUpdateJobIfAlreadyExits(JobDescriptor jobDescriptor) {
    JobDetail jobDetail = jobBuilder.buildJobDetail(jobDescriptor);

    Set<Trigger> triggers = triggerBuilder.buildTriggers(jobDescriptor);

    try {
      log.info("Scheduling job with key: {}", jobDetail.getKey());

      // Check if the job already exists in the scheduler
      JobDetail existingJob = scheduler.getJobDetail(jobDetail.getKey());
      if (existingJob != null) {
        // Job already exists, update its JobDataMap
        JobDataMap jobDataMap = existingJob.getJobDataMap();
        // Update the necessary data in the jobDataMap based on your requirements
        jobDataMap.put("someKey", "someValue");
        // You can update any other data as needed

        // Reschedule the existing job with the updated JobDataMap
        scheduler.addJob(existingJob, true, true);
        log.info("Existing job with key: {} updated and rescheduled.", jobDetail.getKey());
      } else {
        // Job does not exist, schedule the new one
        scheduler.scheduleJob(jobDetail, triggers, false);
        log.info("Job with key: {} scheduled.", jobDetail.getKey());
      }
    } catch (SchedulerException e) {
      log.error("Could not schedule job with key: {}, error: {}", jobDetail.getKey(), e.getLocalizedMessage(), e);
      throw new IllegalArgumentException(e.getLocalizedMessage());
    }
  }

  /**
   * @param jobDescriptor
   * IF JOB WITH NAME ALREADY EXISTS, DELETE IT AND CREATE NEW WITH SAME NAME
   */
  public void createJobDeletingExistingJob(JobDescriptor jobDescriptor) {
    JobDetail jobDetail = jobBuilder.buildJobDetail(jobDescriptor);

    Set<Trigger> triggers = triggerBuilder.buildTriggers(jobDescriptor);

    try {
      log.info("Scheduling job with key: {}", jobDetail.getKey());

      // Check if the job already exists in the scheduler
      JobDetail existingJob = scheduler.getJobDetail(jobDetail.getKey());
      if (existingJob != null) {
        // Job already exists, remove it before scheduling the new one
        scheduler.deleteJob(jobDetail.getKey());
        log.info("Existing job with key: {} removed before scheduling the new one.", jobDetail.getKey());
      }

      scheduler.scheduleJob(jobDetail, triggers, Boolean.FALSE);

      log.info("Job with key: {} scheduled", jobDetail.getKey());
    } catch (SchedulerException e) {
      log.error("Could not schedule job with key: {}, error: {}", jobDetail.getKey(), e.getLocalizedMessage(), e);
      throw new IllegalArgumentException(e.getLocalizedMessage());
    }
  }


  /**
   * @param group
   * @param name
   * @param descriptor UPDATES THE EXISTING JOB WITH GIVEN GROUP AND NAME
   */
  public void updateJob(String group, String name, JobDescriptor descriptor) {
    try {
      JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
      if (Objects.nonNull(oldJobDetail)) {

        JobDataMap jobDataMap = new JobDataMap(descriptor.getData());

        JobBuilder jb = oldJobDetail.getJobBuilder();

        JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();

        scheduler.addJob(newJobDetail, true);

        log.info("Updated job with key:{}", newJobDetail.getKey());
        return;
      }
      log.warn("Could not find job with key:{}.{} to update", group, name);

    } catch (SchedulerException e) {
      log.error("Could not find job with key:{}.{} to update", group, name, e);
    }
  }

  /**
   * @param group
   * @param name
   * @param descriptor
   * @return REPLACES JOB WITH GROUP AND NAME WITH THE NEW ONE GIVEN IN JOB DESCRIPTOR
   */
  public JobDescriptor replaceJob(String group, String name, JobDescriptor descriptor) {
    descriptor.setGroup(group);
    descriptor.setName(name);

    JobDetail jobDetail = jobBuilder.buildJobDetail(descriptor);

    Set<Trigger> triggersForJob = triggerBuilder.buildTriggers(descriptor);

    log.info("Replacing job with key:{}", jobDetail.getKey());

    try {
      scheduler.scheduleJob(jobDetail, triggersForJob, true);
      log.info("Job with saved sucessfully key:{}", jobDetail.getKey());
    } catch (SchedulerException e) {
      log.error(
          "Could not save job key:{},error:{}", jobDetail.getKey(), e.getLocalizedMessage(), e);
      throw new IllegalArgumentException(e.getLocalizedMessage());
    }
    return descriptor;
  }

  /**
   * @param group
   * @param name DELETES JOB WITH KEY NAME AND GROUP
   */
  public void deleteJob(String group, String name) {
    try {
      scheduler.deleteJob(jobKey(name, group));
      log.info("Deleted job with key:{}.{}", group, name);
    } catch (SchedulerException e) {
      log.error("Could not delete job with key:{}.{} due to error", group, name, e);
    }
  }

  /**
   * @param group
   * @param name PAUSES JOB
   */
  public void pauseJob(String group, String name) {
    try {
      scheduler.pauseJob(jobKey(name, group));
      log.info("Paused job with key:{}.{}", group, name);
    } catch (SchedulerException e) {
      log.error("Could not pause job with key:{}.{} due to error", group, name, e);
    }
  }

  /**
   * @param group
   * @param name RESUMES JOB
   */
  public void resumeJob(String group, String name) {
    try {
      scheduler.resumeJob(jobKey(name, group));
      log.info("Resumed job with key:{}.{}", group, name);
    } catch (SchedulerException e) {
      log.error("Could not resume job with key:{}.{} due to error", group, name, e);
    }
  }

  public Optional<JobDescriptor> findJob(String group, String name) {
    // @formatter:off
    try {
      JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
      if (Objects.nonNull(jobDetail))
        return Optional.of(
            jobBuilder.buildDescriptor(
                jobDetail, scheduler.getTriggersOfJob(jobKey(name, group))));
    } catch (SchedulerException e) {
      log.error(
          "Could not find job with key:{}.{},error:{}", group, name, e.getLocalizedMessage(), e);
    }
    // @formatter:on
    log.warn("Could not find job with key:{}.{}", group, name);

    return Optional.empty();
  }


}
