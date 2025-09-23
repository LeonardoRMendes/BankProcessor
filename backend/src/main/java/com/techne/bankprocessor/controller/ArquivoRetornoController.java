package com.techne.bankprocessor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techne.bankprocessor.dto.ArquivoRetornoDTO;
import com.techne.bankprocessor.entity.ArquivoRetorno;
import com.techne.bankprocessor.mapper.ArquivoRetornoMapper;
import com.techne.bankprocessor.service.ArquivoRetornoService;

@RestController
@RequestMapping("/api/arquivos-retorno")
public class ArquivoRetornoController {

    @Autowired
    private ArquivoRetornoService arquivoRetornoService;
    
    @Autowired
    private ArquivoRetornoMapper arquivoRetornoMapper;

    @GetMapping
    public ResponseEntity<List<ArquivoRetornoDTO>> getAllArquivosRetorno() {
        List<ArquivoRetornoDTO> arquivos = arquivoRetornoService.getAll();
        return ResponseEntity.ok(arquivos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArquivoRetornoDTO> getArquivoRetornoById(@PathVariable Long id) {
        ArquivoRetornoDTO arquivo = arquivoRetornoService.getById(id);
        return ResponseEntity.ok(arquivo);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ArquivoRetornoDTO>> getArquivosByJobId(@PathVariable Long jobId) {
        List<ArquivoRetorno> arquivos = arquivoRetornoService.findByJobId(jobId);
        List<ArquivoRetornoDTO> arquivosDTO = arquivos.stream()
            .map(arquivoRetornoMapper::toDTO)
            .toList();
        return ResponseEntity.ok(arquivosDTO);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<String> getArquivoContent(@PathVariable Long id) {
        ArquivoRetornoDTO arquivo = arquivoRetornoService.getById(id);
        return ResponseEntity.ok(arquivo.getConteudo());
    }
}