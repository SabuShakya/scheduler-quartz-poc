package com.sabu.schedulerquartzpoc.builder;

import static org.quartz.JobBuilder.newJob;

import com.sabu.schedulerquartzpoc.job.AbstractJob;
import com.sabu.schedulerquartzpoc.model.TriggerDescriptor;
import com.sabu.schedulerquartzpoc.model.JobDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

/**
 * @author : sabu.shakya
 * @created : 2023-06-27
 **/
@Component
public class JobBuilderUtil {

  public JobDetail buildJobDetail(JobDescriptor jobDescriptor) {
    JobDataMap jobDataMap = new JobDataMap(jobDescriptor.getData());

    Class<? extends AbstractJob> jobType = getJobType(jobDescriptor.getType());

    return newJob()
        .ofType(jobType)
        .storeDurably(true)
        .withIdentity(jobDescriptor.getName(), jobDescriptor.getGroup())
        .withDescription(jobDescriptor.getDescription())
        .usingJobData(jobDataMap)
        .build();

  }

  @SuppressWarnings("unchecked")
  private Class<? extends AbstractJob> getJobType(String classType) {
    Class<? extends AbstractJob> jobType = null;
    try {
      jobType = (Class<? extends AbstractJob>) Class.forName(classType);
    } catch (ClassNotFoundException e) {
      // throw exception
      throw new RuntimeException(e);
    }

    return jobType;
  }

  @SuppressWarnings("unchecked")
  public JobDescriptor buildDescriptor(
      JobDetail jobDetail,
      List<? extends Trigger> triggersOfJob) {

    List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();

    for (Trigger trigger : triggersOfJob) {
      triggerDescriptors.add(TriggerBuilderUtil.buildDescriptor(trigger));
    }

    return JobDescriptor
        .builder()
        .name(jobDetail.getKey().getName())
        .group(jobDetail.getKey().getGroup())
        .triggerDescriptors(triggerDescriptors)
        .build();

  }

}
