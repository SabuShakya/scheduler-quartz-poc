package com.sabu.schedulerquartzpoc.configuration;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @author : sabu.shakya
 * @created : 2023-06-21
**/
@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {

  private final QuartzProperties quartzProperties;

  private final ApplicationContext applicationContext;

  private Properties getQuartzProperties() {
    Properties properties = new Properties();
    properties.putAll(quartzProperties.getProperties());

    return properties;
  }


  /**
   * When Spring will use SchedulerFactoryBean to create Scheduler,
   * SchedulerFactoryBean will set this SpringBeanJobFactory in the Scheduler.
   * Then upon each trigger fire, createJobInstance of this SpringBeanJobFactory will be called
   * And we're explicitly weaving the beans from application context.
   * * @return
   */
  @Bean
  public SpringBeanJobFactory createSpringBeanJobFactory() {
    return new SpringBeanJobFactory() {
      @Override
      protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
        return job;
      }
    };
  }


  /**
   * we create only SchedulerFactoryBean and spring implicitly uses it to create Scheduler
   * @return SchedulerFactoryBean
   */
  @Bean
  public SchedulerFactoryBean createSchedulerFactoryBean(SpringBeanJobFactory jobFactory) {
    jobFactory.setApplicationContext(applicationContext);

    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();

    schedulerFactory.setQuartzProperties(getQuartzProperties());
    schedulerFactory.setAutoStartup(Boolean.TRUE);
    schedulerFactory.setApplicationContext(applicationContext);
    schedulerFactory.setJobFactory(jobFactory);
    schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");

    return schedulerFactory;
  }

}
