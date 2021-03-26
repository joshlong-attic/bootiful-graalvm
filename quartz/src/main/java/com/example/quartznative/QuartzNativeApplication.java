package com.example.quartznative;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.LocalTaskExecutorThreadPool;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.ResourceLoaderClassLoadHelper;

import java.time.Instant;
//todo turn this into a pattern
@ResourceHint  (
	patterns = "classpath:org/quartz/impl/\"\n" +
		"\t\t\t\t+ \"jdbcjobstore/tables_@@platform@@.sql"
)
@NativeHint(
	trigger = QuartzJobBean.class,
	types = {
		@TypeHint(types = SampleJob.class),
		@TypeHint(
			access = AccessBits.ALL,
			types = {
				org.quartz.simpl.RAMJobStore. class,
				LocalDataSourceJobStore.class,
				SimpleThreadPool.class,
				ResourceLoaderClassLoadHelper.class,
				LocalTaskExecutorThreadPool.class,
				StdSchedulerFactory.class,
			}
		)}
)
@SpringBootApplication
public class QuartzNativeApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzNativeApplication.class, args);
	}

	@Bean
	JobDetail sampleJobDetail(@Value("${greetings.name:World}") String name) {
		return JobBuilder
			.newJob(SampleJob.class) //
			.withIdentity("sampleJob") //
			.usingJobData("name", name) //
			.storeDurably() //
			.build();
	}

	@Bean
	Trigger sampleJobTrigger(JobDetail sampleJobDetail) {
		var scheduleBuilder = SimpleScheduleBuilder
			.simpleSchedule() //
			.withIntervalInSeconds(2) //
			.withRepeatCount(5);

		return TriggerBuilder//
			.newTrigger()//
			.forJob(sampleJobDetail)//
			.withIdentity("sampleTrigger") //
			.withSchedule(scheduleBuilder) //
			.build();
	}
	//
}


class SampleJob extends QuartzJobBean {

	private String name;

	// Invoked if a Job data map entry with that name
	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
		throws JobExecutionException {
		System.out.println(String.format("Hello %s @ " + Instant.now() + "!", this.name));

	}

}