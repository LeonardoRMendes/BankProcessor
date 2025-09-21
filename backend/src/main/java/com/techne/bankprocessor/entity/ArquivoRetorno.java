package com.techne.bankprocessor.entity;

import java.time.LocalDateTime;

import com.techne.bankprocessor.model.StatusArquivo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ARQUIVO_RETORNO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArquivoRetorno {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "jobId", nullable = false)
    private Job job;
    
    @Column(name = "nomeArquivo", nullable = false, length = 50)
    private String nomeArquivo;
    
    @Column(name = "conteudo", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String conteudo;
    
    @Column(name = "dataProcessamento")
    private LocalDateTime dataProcessamento;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private StatusArquivo status;

}