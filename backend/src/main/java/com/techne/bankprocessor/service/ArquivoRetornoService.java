package com.techne.bankprocessor.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techne.bankprocessor.dto.ArquivoRetornoDTO;
import com.techne.bankprocessor.dto.CreateArquivoRetornoDTO;
import com.techne.bankprocessor.entity.ArquivoRetorno;
import com.techne.bankprocessor.entity.Job;
import com.techne.bankprocessor.mapper.ArquivoRetornoMapper;
import com.techne.bankprocessor.model.StatusArquivo;
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

    public List<ArquivoRetorno> findByStatus(StatusArquivo status) {
        return arquivoRetornoRepository.findByStatus(status);
    }
    
    public List<ArquivoRetorno> findByJobId(Long jobId) {
		return arquivoRetornoRepository.findByJobId(jobId);
	}

    @Transactional
    public ArquivoRetorno createArquivoRetorno(Job job, String nomeArquivo, String conteudo) {
        ArquivoRetorno arquivo = ArquivoRetorno.builder()
            .job(job)
            .nomeArquivo(nomeArquivo)
            .conteudo(conteudo)
            .status(StatusArquivo.PENDENTE)
            .dataProcessamento(LocalDateTime.now())
            .build();
        
        return arquivoRetornoRepository.save(arquivo);
    }

    @Transactional
    public void updateStatus(Long id, StatusArquivo status) {
        arquivoRetornoRepository.findById(id).ifPresent(arquivo -> {
            arquivo.setStatus(status);
            arquivo.setDataProcessamento(LocalDateTime.now());
            arquivoRetornoRepository.save(arquivo);
        });
    }
}