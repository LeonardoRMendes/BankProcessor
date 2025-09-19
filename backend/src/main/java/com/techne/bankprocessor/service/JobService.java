package com.techne.bankprocessor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techne.bankprocessor.dto.CreateJobDTO;
import com.techne.bankprocessor.dto.JobDTO;
import com.techne.bankprocessor.entity.Job;
import com.techne.bankprocessor.mapper.JobMapper;
import com.techne.bankprocessor.model.StatusJob;
import com.techne.bankprocessor.repository.JobRepository;
import com.techne.bankprocessor.scheduler.SchedulerService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private ArquivoRetornoService arquivoRetornoService;

    @Transactional
    public JobDTO createJob(CreateJobDTO dto) {
    	validateCronExpression(dto.getCronExpression());
    	Job job = jobMapper.toEntity(dto);
        job.setStatus(StatusJob.AGENDADO);
        job = jobRepository.save(job);
        schedulerService.scheduleJob(job.getId(), job.getNome(), job.getCronExpression());
        updateNextExecution(job.getId());
        job = jobRepository.findById(job.getId()).orElseThrow();
        
        return jobMapper.toDTO(job);
    }

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
            .map(jobMapper::toDTO)
            .collect(Collectors.toList());
    }

    public JobDTO getJobById(Long id) {
        return jobRepository.findById(id)
            .map(jobMapper::toDTO)
            .orElse(null);
    }

    @Transactional
    public JobDTO updateJob(Long id, CreateJobDTO dto) {
        return jobRepository.findById(id).map(job -> {
        	validateCronExpression(dto.getCronExpression());
        	if(job.getStatus() == StatusJob.DESATIVADO) {
            	throw new IllegalArgumentException("Job está desativado e não pode ser alterado.");
            }
            else {
	            String oldCronExpression = job.getCronExpression();
	            
	            job.setNome(dto.getNome());
	            job.setCronExpression(dto.getCronExpression());
	            job = jobRepository.save(job);
	            
	            
	            if (!oldCronExpression.equals(dto.getCronExpression())) {
	                schedulerService.rescheduleJob(job.getId(), job.getNome(), job.getCronExpression());
	            }
	            
	            return jobMapper.toDTO(job);
			}
        }).orElseThrow(() -> new EntityNotFoundException("O Job com o ID especificado não foi encontrado."));
    }
    
    public JobDTO updateLastExecution(Long id, java.time.LocalDateTime lastExecution) {
		return jobRepository.findById(id).map(job -> {
			job.setUltimaExecucao(lastExecution);
			job = jobRepository.save(job);
			return jobMapper.toDTO(job);
		}).orElseThrow(() -> new EntityNotFoundException("O Job com o ID especificado não foi encontrado."));
	}

    public JobDTO updateNextExecution(Long id) {
		return jobRepository.findById(id).map(job -> {
			java.time.LocalDateTime nextExecution = schedulerService.getNextExecutionTime(job.getId());
			job.setProximaExecucao(nextExecution);
			job = jobRepository.save(job);
			return jobMapper.toDTO(job);
		}).orElseThrow(() -> new EntityNotFoundException("O Job com o ID especificado não foi encontrado."));
	}

    public void deleteJob(Long id) {
		Job job = jobRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("O Job com o ID especificado não foi encontrado."));

    	
    	schedulerService.unscheduleJob(id);
        boolean hardDelete = arquivoRetornoService.findByJobId(id).isEmpty();
        //SoftDelete
        if (!hardDelete) {
			
			if(job.getStatus() == StatusJob.DESATIVADO) {
				throw new IllegalArgumentException("Job já está desativado.");
			}
			else{job.setStatus(StatusJob.DESATIVADO);
			job.setProximaExecucao(null);
			jobRepository.save(job);
			}
			return;
		}
        //HardDelete
        else {
			jobRepository.deleteById(id);
		}
    }

    @Transactional
    public void updateJobStatus(Long id, StatusJob status) {
        jobRepository.findById(id).ifPresent(job -> {
            job.setStatus(status);
            jobRepository.save(job);
        });
    }
    
    private void validateCronExpression(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Expressão Cron inválida.");
        }
    }
    
}