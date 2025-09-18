package com.techne.bankprocessor.dto;

import org.springframework.scheduling.support.CronExpression;

import lombok.Data;

@Data
public class CreateJobDTO {
    private String nome;
	private CronExpression cronExpression;
}