package com.techne.bankprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techne.bankprocessor.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

}
