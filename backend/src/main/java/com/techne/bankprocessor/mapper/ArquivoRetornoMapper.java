package com.techne.bankprocessor.mapper;

import org.springframework.stereotype.Component;

import com.techne.bankprocessor.dto.ArquivoRetornoDTO;
import com.techne.bankprocessor.dto.CreateArquivoRetornoDTO;
import com.techne.bankprocessor.entity.ArquivoRetorno;

@Component
public class ArquivoRetornoMapper {
    public ArquivoRetorno toEntity(CreateArquivoRetornoDTO dto) {
        ArquivoRetorno entity = new ArquivoRetorno();
        entity.setNomeArquivo(dto.getNomeArquivo());
        entity.setConteudo(dto.getConteudo());
        return entity;
    }

    public ArquivoRetornoDTO toDTO(ArquivoRetorno entity) {
        ArquivoRetornoDTO dto = new ArquivoRetornoDTO();
        dto.setId(entity.getId());
        dto.setNomeArquivo(entity.getNomeArquivo());
        dto.setConteudo(entity.getConteudo());
        dto.setDataProcessamento(entity.getDataProcessamento());
        dto.setStatus(entity.getStatus());
        return dto;
    }

}