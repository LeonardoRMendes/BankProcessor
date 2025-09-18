package com.techne.bankprocessor.dto;

import lombok.Data;

@Data
public class CreateArquivoRetornoDTO {
    private String nomeArquivo;
    private String conteudo;
}