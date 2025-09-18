package com.techne.bankprocessor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techne.bankprocessor.dto.ArquivoRetornoDTO;
import com.techne.bankprocessor.dto.CreateArquivoRetornoDTO;
import com.techne.bankprocessor.entity.ArquivoRetorno;
import com.techne.bankprocessor.mapper.ArquivoRetornoMapper;
import com.techne.bankprocessor.repository.ArquivoRetornoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ArquivoRetornoService {
    @Autowired
    private ArquivoRetornoRepository arquivoRetornoRepository;
    @Autowired
    private ArquivoRetornoMapper arquivoRetornoMapper;

    @Transactional
    public ArquivoRetornoDTO save(CreateArquivoRetornoDTO dto) {
        ArquivoRetorno entity = arquivoRetornoMapper.toEntity(dto);
        entity = arquivoRetornoRepository.save(entity);
        return arquivoRetornoMapper.toDTO(entity);
    }

    @Transactional
    public ArquivoRetornoDTO update(Long id, CreateArquivoRetornoDTO dto) {
        return arquivoRetornoRepository.findById(id).map(arquivo -> {
            arquivo.setNomeArquivo(dto.getNomeArquivo());
            arquivo = arquivoRetornoRepository.save(arquivo);
            return arquivoRetornoMapper.toDTO(arquivo);
        }).orElseThrow(() -> new EntityNotFoundException("ArquivoRetorno não encontrado com id: " + id));
    }


    public List<ArquivoRetornoDTO> getAll() {
        return arquivoRetornoRepository.findAll().stream()
            .map(arquivoRetornoMapper::toDTO)
            .collect(Collectors.toList());
    }

    public ArquivoRetornoDTO getById(Long id) {
        return arquivoRetornoRepository.findById(id)
            .map(arquivoRetornoMapper::toDTO)
            .orElseThrow(() -> new EntityNotFoundException("ArquivoRetorno não encontrado com id: " + id));
    }

    public void delete(Long id) {
        arquivoRetornoRepository.deleteById(id);
    }
}