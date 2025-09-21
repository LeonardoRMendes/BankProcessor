-- Create database
CREATE DATABASE bankprocessor;
GO

-- Use the database
USE bankprocessor;
GO

-- Create Job table
CREATE TABLE JOB (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome NVARCHAR(255) NOT NULL,
    cronExpression NVARCHAR(50) NOT NULL,
    status INT NOT NULL,
    ultimaExecucao DATETIME2(6) NULL,
    proximaExecucao DATETIME2(6) NULL
);

-- Create ArquivoRetorno table
CREATE TABLE ARQUIVO_RETORNO (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    jobId BIGINT NOT NULL,
    nomeArquivo NVARCHAR(50) NOT NULL,
    conteudo NVARCHAR(MAX) NOT NULL,
    dataProcessamento DATETIME2(6) NULL,
    status INT NOT NULL,
    FOREIGN KEY (jobId) REFERENCES JOB(id)
);

-- Create indexes for better performance
CREATE INDEX IX_ARQUIVO_RETORNO_jobId ON ARQUIVO_RETORNO(jobId);
CREATE INDEX IX_JOB_status ON JOB(status);