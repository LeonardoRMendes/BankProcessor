package com.techne.bankprocessor.dto;

import java.time.LocalDateTime;

import org.springframework.scheduling.support.CronExpression;

import com.techne.bankprocessor.model.StatusJob;

import lombok.Data;

@SuppressWarnings("unused")
@Data
public class JobDTO {
    private Long id;
    private String nome;
	private CronExpression cronExpression;
	private StatusJob status;
	private LocalDateTime ultimaExecucao;
	private LocalDateTime proximaExecucao;
}