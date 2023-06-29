package com.sabu.schedulerquartzpoc.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @author : sabu.shakya
 * @created : 2023-06-28
 **/
@Slf4j
@Component
public class TestJob extends AbstractJob {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // LOGIC TO EXECUTE
    this.log(context);
    log.info("Executing test job");
    JobDataMap jobDataMap = this.getJobDataMap(context);
    log.info("JOB DATA", jobDataMap);
  }
}
