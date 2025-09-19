package com.techne.bankprocessor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techne.bankprocessor.entity.ArquivoRetorno;
import com.techne.bankprocessor.model.StatusArquivo;

@Repository
public interface ArquivoRetornoRepository extends JpaRepository<ArquivoRetorno, Long> {
    
    List<ArquivoRetorno> findByStatus(StatusArquivo status);

    List<ArquivoRetorno> findByJobId(Long jobId);
}