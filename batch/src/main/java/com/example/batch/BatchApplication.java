package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Bean
	Step step(StepBuilderFactory sbf) {
		return sbf
			.get("the-step")
			.tasklet((stepContribution, chunkContext) -> {
				System.out.println("finished the Batch job!");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	Job job(JobBuilderFactory jbf, Step step) {
		return jbf
			.get("the-job")
			.start(step)
			.build();
	}
}