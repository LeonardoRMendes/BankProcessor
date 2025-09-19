package com.techne.bankprocessor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techne.bankprocessor.dto.CreateJobDTO;
import com.techne.bankprocessor.dto.JobDTO;
import com.techne.bankprocessor.entity.ArquivoRetorno;
import com.techne.bankprocessor.service.ArquivoRetornoService;
import com.techne.bankprocessor.service.JobService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;  
    
    @Autowired
    private ArquivoRetornoService arquivoRetornoService;
    
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody CreateJobDTO createJobDTO) {
    	try {
        	JobDTO jobDTO = jobService.createJob(createJobDTO);
            return new ResponseEntity<>(jobDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<JobDTO> jobDTOs = jobService.getAllJobs();
        return ResponseEntity.ok(jobDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        JobDTO jobDTO = jobService.getJobById(id);
        if (jobDTO != null) {
            return ResponseEntity.ok(jobDTO);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/{id}/arquivos")
    public ResponseEntity<?> getArquivosByJobId(@PathVariable Long id) {
        JobDTO jobDTO = jobService.getJobById(id);
        if (jobDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("O Job com o ID especificado não foi encontrado.");
        }
        try {
            List<ArquivoRetorno> arquivos = arquivoRetornoService.findByJobId(id);
            return ResponseEntity.ok(arquivos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody CreateJobDTO createJobDTO) {
        try {
        	JobDTO updatedJob = jobService.updateJob(id, createJobDTO);            
        	updatedJob = jobService.updateNextExecution(updatedJob.getId());
        	return ResponseEntity.ok(updatedJob);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
    	try {
    		jobService.deleteJob(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
        }
    }
}