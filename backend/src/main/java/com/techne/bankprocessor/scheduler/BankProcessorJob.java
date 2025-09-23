package com.techne.bankprocessor.scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techne.bankprocessor.entity.ArquivoRetorno;
import com.techne.bankprocessor.model.StatusArquivo;
import com.techne.bankprocessor.model.StatusJob;
import com.techne.bankprocessor.repository.JobRepository;
import com.techne.bankprocessor.service.ArquivoRetornoService;
import com.techne.bankprocessor.service.JobService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BankProcessorJob implements Job {

    @Autowired
    private JobService jobService;
    
    @Autowired
    private ArquivoRetornoService arquivoRetornoService;
    
    @Autowired
    private JobRepository jobRepository;

    private static final String RETORNOS = "/home/leo/techne_project/retornos_bancarios";
    private static final String PENDENTES = "/home/leo/techne_project/retornos_pendentes";
    private static final String PROCESSADOS = "/home/leo/techne_project/retornos_processados";
    private static final String ERRO = "/home/leo/techne_project/retornos_erros";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");        
                
        log.info("Executing routine for Job ID: {}", jobId);

        try {
            jobService.updateJobStatus(jobId, StatusJob.PROCESSANDO);            
            
            jobService.updateLastExecution(jobId, LocalDateTime.now());
            jobService.updateNextExecution(jobId);
            
            executeRoutine(jobId);            
            
            processPendingFiles();
            
            jobService.updateJobStatus(jobId, StatusJob.CONCLUIDO);
            
        } catch (Exception e) {
            log.error("Error executing job {}: {}", jobId, e.getMessage(), e);
            jobService.updateJobStatus(jobId, StatusJob.FALHA);
            throw new JobExecutionException(e);
        }
    }
        
    private void executeRoutine(Long jobId) {
        try {
            log.info("Starting routine execution for Job ID: {}", jobId);
            
            com.techne.bankprocessor.entity.Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
            
            Path sourceDir = Paths.get(RETORNOS);
            
            Files.list(sourceDir)
                .filter(file -> file.toString().endsWith(".txt"))
                .forEach(file -> processSourceFile(job, file));
                
            log.info("Routine executed for Job ID: {}", jobId);
            
        } catch (IOException e) {
            log.error("Error processing source files for job {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Failed to process source files", e);
        }
    }

    private void processSourceFile(com.techne.bankprocessor.entity.Job job, Path sourceFile) {
        try {
            String content = Files.readString(sourceFile);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String newFileName = String.format("job-%d_%s.txt", job.getId(), timestamp);
            
            Path pendingDir = Paths.get(PENDENTES);
            Files.createDirectories(pendingDir);
            Path pendingFile = pendingDir.resolve(newFileName);
            Files.move(sourceFile, pendingFile);
            
            arquivoRetornoService.createArquivoRetorno(job, newFileName, content);
            
            log.info("Created pending file: {} for job: {}", newFileName, job.getId());
            
        } catch (IOException e) {
            log.error("Error processing source file {}: {}", sourceFile, e.getMessage(), e);
        }
    }

    private void processPendingFiles() throws InterruptedException {
        log.info("Processing pending files...");
        
        List<ArquivoRetorno> pendingFiles = arquivoRetornoService.findByStatus(StatusArquivo.PENDENTE);
        
        for (ArquivoRetorno arquivo : pendingFiles) {
            processFile(arquivo);
        }
        
        log.info("Finished processing pending file");
    }
    
    private void processFile(ArquivoRetorno arquivo) throws InterruptedException {
        try {
            log.info("Processing file: {}", arquivo.getNomeArquivo());

            Path pendingFile = Paths.get(PENDENTES, arquivo.getNomeArquivo());
            
            if (!Files.exists(pendingFile)) {
                log.warn("File not found: {}", pendingFile);
                return;
            }
            
            String content = Files.readString(pendingFile);
            boolean success = processFileContent(content);
            
            Thread.sleep(10000); 

            if (success) {
                Path processedDir = Paths.get(PROCESSADOS);
                Files.createDirectories(processedDir);
                Path processedFile = processedDir.resolve(arquivo.getNomeArquivo());
                Files.move(pendingFile, processedFile);
                
                arquivoRetornoService.updateStatus(arquivo.getId(), StatusArquivo.PROCESSADO);
                
                log.info("File processed successfully: {}", arquivo.getNomeArquivo());
                
            } else {
                Path errorDir = Paths.get(ERRO);
                Files.createDirectories(errorDir);
                Path errorFile = errorDir.resolve(arquivo.getNomeArquivo());
                Files.move(pendingFile, errorFile);
                
                arquivoRetornoService.updateStatus(arquivo.getId(), StatusArquivo.ERRO);
                
                log.warn("File processing failed: {}", arquivo.getNomeArquivo());
            }
            
        } catch (IOException e) {
            log.error("Error processing file {}: {}", arquivo.getNomeArquivo(), e.getMessage(), e);
            
            try {
                Path errorDir = Paths.get(ERRO);
                Files.createDirectories(errorDir);
                Path pendingFile = Paths.get(PENDENTES, arquivo.getNomeArquivo());
                Path errorFile = errorDir.resolve(arquivo.getNomeArquivo());
                Files.move(pendingFile, errorFile);
                
                arquivoRetornoService.updateStatus(arquivo.getId(), StatusArquivo.ERRO);
                
            } catch (IOException moveError) {
                log.error("Error moving file to error directory: {}", moveError.getMessage(), moveError);
            }
        }
    }
    
    
    private boolean processFileContent(String content) {
        try {
            String[] lines = content.split("\\r?\\n");
                        
            String headerLine = lines[0];
            if (!processHeader(headerLine)) {
                return false;
            }
            
            boolean allTransactionsSuccessful = true;
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].trim().isEmpty()) {
                    if (!processTransaction(lines[i], i + 1)) {
                        allTransactionsSuccessful = false;
                    }
                }
            }
            
            return allTransactionsSuccessful;
            
        } catch (Exception e) {
            log.error("Error processing file content: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private boolean processHeader(String headerLine) {
        try {
            headerLine = headerLine.trim();       
            
            if (headerLine.length() < 34) {
                log.error("Header line too short: {} (length: {})", headerLine, headerLine.length());
                return false;
            }
            
            if (headerLine.length() > 34) {
                log.error("Header line too long: {} (length: {})", headerLine, headerLine.length());
                return false;
            }
            
            String dataGeracao = headerLine.substring(0, 8);
            String horaGeracao = headerLine.substring(8, 14);
            String empresa = headerLine.substring(14, 23).trim();
            String numeroLote = headerLine.substring(24, Math.min(headerLine.length(), 33));
            
            log.info("Header - Data: {}, Hora: {}, Empresa: {}, Lote: {}", 
                    dataGeracao, horaGeracao, empresa, numeroLote);
            
            return true;
            
        } catch (Exception e) {
            log.error("Error processing header: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private boolean processTransaction(String transactionLine, int lineNumber) {
        try {        	
            if (transactionLine.length() < 53) {
                log.error("Transaction line {} too short: {}", lineNumber, transactionLine);
                return false;
            }
            
            if (transactionLine.length() > 53) {
                log.error("Transaction line {} too long: {}", lineNumber, transactionLine);
                return false;
            }
         
            String tipoTransacao = transactionLine.substring(0, 1);
            String valorStr = transactionLine.substring(2, 11);
            String dataTransacao = transactionLine.substring(12, 20);
            String descricao = transactionLine.substring(20, 49).trim();
            String codigoOcorrencia = transactionLine.substring(50);
            
            double valor = Double.parseDouble(valorStr) / 100.0;
            String valorFormatado = String.format("R$ %.2f", valor);
            log.info("Transaction Line {} - Tipo: {}, Valor: R$ {}, Data: {}, Descrição: {}, Código: {}" + "\n" + transactionLine, 
                    lineNumber, tipoTransacao, valorFormatado, dataTransacao, descricao, codigoOcorrencia);
            
            return true;
            
        } catch (Exception e) {
            log.error("Error processing transaction line {}: {}", lineNumber, e.getMessage(), e);
            return false;
        }
    }
}