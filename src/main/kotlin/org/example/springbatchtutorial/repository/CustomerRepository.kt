package org.example.springbatchtutorial.repository

import org.example.springbatchtutorial.domain.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long>
