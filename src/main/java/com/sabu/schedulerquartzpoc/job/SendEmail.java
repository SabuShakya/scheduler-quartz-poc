package com.sabu.schedulerquartzpoc.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

/**
 * @author : sabu.shakya
 * @created : 2023-07-06
 **/
@Slf4j
@Service
public class SendEmail extends AbstractJob {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

  }
}
