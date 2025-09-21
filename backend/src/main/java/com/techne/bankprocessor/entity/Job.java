package com.techne.bankprocessor.entity;

import java.time.LocalDateTime;

import com.techne.bankprocessor.model.StatusJob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "JOB")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nome", nullable = false, length = 255)
	private String nome;

	@Column(name = "cronExpression", nullable = false, length = 50)
	private String cronExpression;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private StatusJob status;

	@Column(name = "ultimaExecucao", nullable = true)
	private LocalDateTime ultimaExecucao;

	@Column(name = "proximaExecucao", nullable = true)
	private LocalDateTime proximaExecucao;

}