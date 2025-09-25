# Bank Processor Project

This project is a web application designed to process bank transaction files. It consists of a Spring Boot backend that handles file processing via scheduled jobs made with Quartz and an Angular frontend for user interaction.

# Prerequisites

Ensure you have the following software installed on your system:

	•  Java: Version 21 or higher
	•  Maven: Version 3.9 or higher
	•  Angular CLI: Version 20 or higher
	•  Docker: Latest stable version

# Backend 

 - Navigate to the backend project directory:
```console   
      cd bankprocessor/backend
```      
 - Run the startup script. This script will build the application and start the necessary services using Docker Compose.
```console   
      ./start-app.sh
```
The backend server will start on http://localhost:8080.


# Frontend 

 - Navigate to the frontend project directory:
```console   
      cd bankprocessor/frontend
```
 - Install the required npm packages:
```console   
      npm install
```
- Start the Angular development server:
```console   
      ng serve
```
The frontend application will be available at http://localhost:4200.

# API Endpoints
##JOB

 - GET /api/jobs: Retrieves a list of all jobs.
```json  
[    
    {
        "id": 1,
        "nome": "File Processor 1",
        "cronExpression": "* * * * * ?",
        "status": "AGENDADO",
        "ultimaExecucao": "2024-07-26T01:00:00",
        "proximaExecucao": "2024-07-27T01:00:00"
    },
    {
        "id": 2,
        "nome": "File Processor 2",
        "cronExpression": "* * * * * ?",
        "status": "CONCLUIDO",
        "ultimaExecucao": "2024-07-26T01:00:00",
        "proximaExecucao": "2024-07-27T01:00:00"
    },
    {
        "id": 3,
        "nome": "File Processor 3",
        "cronExpression": "* * * * * ?",
        "status": "DESATIVADO",
        "ultimaExecucao": "2024-07-26T01:00:00",
    }
]
```

 - GET /api/jobs/{id}: Retrieves a Job with the specified value. 
```json 
{
    "id": 1,
    "nome": "File Processor 1",
    "cronExpression": "* * * * * ?",
    "status": "AGENDADO",
    "ultimaExecucao": "2024-07-26T01:00:00",
    "proximaExecucao": "2024-07-27T01:00:00"
}
```

- GET /api/jobs/1/arquivos: Retrieves a list of all Arquivos processed by the job with the specified value.
```json  
[
    {
        "id": 1,
        "job": {
            "id": 1,
            "nome": "Job 1",
            "cronExpression": "* * * * * ?",
            "status": "DESATIVADO",
            "ultimaExecucao": "2025-09-22T22:40:00.014655"
        },
        "nomeArquivo": "job-17_20250922_224000.txt",
        "conteudo": "xxx",
        "dataProcessamento": "2025-09-22T22:40:10.084878",
        "status": "PROCESSADO"
    }
]
```
 - DELETE /api/jobs/: HardDeletes a Job if it has never been run or SoftDeletes if it has associated Arquivos

 - POST /api/jobs: Creates a Job.
```json  
{
"nome": "Processamento de Relatório Mensal",
"cronExpression": "0/45 * * * * ?"
}
```

 - PUT /api/jobs: Updates a Job.
```json  
{
"nome": "Processamento de Relatório Mensal",
"cronExpression": "0/45 * * * * ?"
}
```
##ARQUIVO_RETORNO
 - GET /api/arquivos-retorno: Retrieves a list of all arquivos.
```json  
[
    {
        "id": 1,
        "jobId": 1,
        "jobNome": "Job",
        "nomeArquivo": "job-11_20250922_222430.txt",
        "conteudo": "xxxx",
        "status": "PROCESSADO"
    },
    {
        "id": 2,
        "jobId": 1,
        "jobNome": "Job",
        "nomeArquivo": "xxx",
        "dataProcessamento": "2025-09-22T22:29:20.073326",
        "status": "ERRO"
    }
]
```

 - GET /api/arquivos-retorno/1: Retrieves a Arquivo with the specified value.
```json  
{
  "id": 1,
  "jobId": 1,
  "jobNome": "Job",
  "nomeArquivo": "job-11_20250922_222430.txt",
  "conteudo": "xxxx",
  "status": "PROCESSADO"
}
```

 - GET /api/arquivos-retorno/1/content: Retrieves the content from the Arquivo with the specified value.
>14082025173000EMPRESA_A 0000000001\
P0000001505014082025PAGAMENTO FORNECEDOR ABC      000\
R0000025000014082025RECEBIMENTO CLIENTE XYZ       000

# Bank File Processing (BankProcessorJob)

The core of the file processing is handled by the BankProcessorJob, which is scheduled with Quartz.

## File Paths

The job reads files from a specific directory, processes them, and moves them to other directories based on the outcome (processed, error). These paths are hardcoded in the bankprocessor/backend/src/main/java/com/techne/bankprocessor/scheduler/BankProcessorJob.java file on lines 39-42.

You must update these static path variables to match your local environment.

## Input File Format

The job expects .txt files in the retornos_bancarios directory with a specific fixed-width format. Each file must contain a header line followed by one or more transaction lines.

### Header (34 characters) 
-	Position 1-8: Generation Date (YYYYMMDD)
- Position 9-14: Generation Time (HHMMSS)
-	Position 15-23: Company Name (10 characters, right-padded with spaces)
-	Position 24-33: Batch Number (10 characters)

### Transaction
- Position 1: Transaction Type (1 character)
- Position 2-12: Value (11 digits, no decimal point, e.g., 0000123456 for 1234.56)
- Position 13-20: Transaction Date (YYYYMMDD)
- Position 21-50: Description (30 characters, right-padded with spaces)
- Position 51-53: Occurrence Code (3 characters)

