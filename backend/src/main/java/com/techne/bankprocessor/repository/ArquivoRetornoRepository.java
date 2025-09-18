package com.techne.bankprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techne.bankprocessor.entity.ArquivoRetorno;

@Repository
public interface ArquivoRetornoRepository extends JpaRepository<ArquivoRetorno, Long> {

}