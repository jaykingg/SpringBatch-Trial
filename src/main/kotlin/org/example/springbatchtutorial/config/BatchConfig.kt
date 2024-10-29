package org.example.springbatchtutorial.config

import jakarta.persistence.EntityManagerFactory
import org.example.springbatchtutorial.domain.Customer
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableBatchProcessing
class BatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun job(): Job {
        return JobBuilder("emailJob", jobRepository)
            .start(step())
            .build()
    }

    @Bean
    fun step(): Step {
        return StepBuilder("emailStep", jobRepository)
            .chunk<Customer, Customer>(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build()
    }

    @Bean
    fun reader(): JpaPagingItemReader<Customer> {
        return JpaPagingItemReaderBuilder<Customer>()
            .name("customerReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM Customer c")
            .pageSize(10)
            .build()
    }

    @Bean
    fun processor(): ItemProcessor<Customer, Customer> {
        return ItemProcessor { customer ->
            if (customer.email != null) {
                customer  // 이메일이 있는 고객만 반환하여 Writer로 전달
            } else null // 이메일이 없는 고객은 null 반환하여 필터링
        }
    }

    @Bean
    fun writer(): ItemWriter<Customer> {
        return ItemWriter { customers ->
            customers.forEach { customer ->
                println("Sending email to: ${customer.name} at ${customer.email}")
            }
        }
    }
}
