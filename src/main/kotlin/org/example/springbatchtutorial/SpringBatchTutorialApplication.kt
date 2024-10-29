package org.example.springbatchtutorial

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
class SpringBatchTutorialApplication(
    private val jobLauncher: JobLauncher,
    private val job: Job
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        runJob()
    }

    @Scheduled(cron = "*/10 * * * * *") // 매 정시에 배치 작업 실행
    fun runJob() {
        val jobParameters = JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(job, jobParameters)
    }
}

fun main(args: Array<String>) {
    runApplication<SpringBatchTutorialApplication>(*args)
}
