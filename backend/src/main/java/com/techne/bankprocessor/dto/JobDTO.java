package com.techne.bankprocessor.dto;

import java.time.LocalDateTime;

import com.techne.bankprocessor.model.StatusJob;

import lombok.Data;

@Data
public class JobDTO {
    private Long id;
    private String nome;
	private String cronExpression;
	private StatusJob status;
	private LocalDateTime ultimaExecucao;
	private LocalDateTime proximaExecucao;
}