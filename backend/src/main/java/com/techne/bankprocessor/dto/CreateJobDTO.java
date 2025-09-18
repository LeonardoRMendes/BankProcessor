package com.techne.bankprocessor.dto;

import lombok.Data;

@Data
public class CreateJobDTO {
    private String nome;
	private String cronExpression;
}