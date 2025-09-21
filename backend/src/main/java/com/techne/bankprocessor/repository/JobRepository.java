package com.techne.bankprocessor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techne.bankprocessor.entity.Job;
import com.techne.bankprocessor.model.StatusJob;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
	 List<Job> findByStatusNot(StatusJob status);
}
