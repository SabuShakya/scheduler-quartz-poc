package com.sabu.schedulerquartzpoc.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * @author : sabu.shakya
 * @created : 2023-06-27
 **/
@Slf4j
public abstract class AbstractJob implements Job {

  public void log(JobExecutionContext context) {
    log.info(
        "Processing job with key:{}, description:{}",
        context.getJobDetail().getKey(),
        context.getJobDetail().getDescription());
  }

  public JobDataMap getJobDataMap(JobExecutionContext context) {
    return context.getTrigger().getJobDataMap();
  }

}
