package com.techne.bankprocessor.dto;

import java.time.LocalDateTime;

import com.techne.bankprocessor.model.StatusArquivo;

import lombok.Data;

@Data
public class ArquivoRetornoDTO {
	private Long id;
    private String nomeArquivo;
    private String conteudo;
    private LocalDateTime dataProcessamento;
    private StatusArquivo status;
}