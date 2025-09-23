import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ArquivoRetornoService } from '../arquivo-retorno.service';
import { JobService } from '../job.service';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-arquivo-retorno-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './arquivo-retorno-list.html',
  styleUrls: ['./arquivo-retorno-list.css']
})
export class ArquivoRetornoListComponent implements OnInit {
  arquivos: any[] = [];
  allArquivos: any[] = [];
  jobs: any[] = [];
  selectedJobId: number | null = null;
  isLoading = false;

  isModalOpen = false;
  modalTitle = '';
  fileContent = '';
  isLoadingContent = false;

  constructor(
    private arquivoRetornoService: ArquivoRetornoService,
    private jobService: JobService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadJobs();
    this.loadAllArquivos();
  }

  loadJobs(): void {
    this.jobService.getJobs().subscribe(
      (data: any[]) => {
        this.jobs = data;
      },
      (error: any) => {
        console.error('Erro ao buscar jobs:', error);
      }
    );
  }

  loadAllArquivos(): void {
    this.isLoading = true;
    this.arquivoRetornoService.getAllArquivos().subscribe(
      (data: any[]) => {
        this.allArquivos = data;
        this.arquivos = data;
        this.isLoading = false;
      },
      (error: any) => {
        console.error('Erro ao buscar arquivos de retorno:', error);
        this.isLoading = false;
      }
    );
  }

  searchByJobId(): void {
    if (this.selectedJobId) {
      this.isLoading = true;
      this.arquivoRetornoService.getArquivosByJobId(this.selectedJobId).subscribe(
        (data: any[]) => {
          this.arquivos = data;
          this.isLoading = false;
        },
        (error: any) => {
          console.error('Erro ao buscar arquivos por Job ID:', error);
          this.isLoading = false;
        }
      );
    } else {
      this.arquivos = this.allArquivos;
    }
  }

  clearFilter(): void {
    this.selectedJobId = null;
    this.arquivos = this.allArquivos;
  }

  goBack(): void {
    this.router.navigate(['/jobs']);
  }

  viewDetails(id: number): void {
    const arquivo = this.arquivos.find(a => a.id === id);
    if (arquivo) {
      this.modalTitle = `Conteúdo do Arquivo: ${arquivo.nomeArquivo}`;
      this.isModalOpen = true;
      this.isLoadingContent = true;
      this.fileContent = '';

      this.arquivoRetornoService.getArquivoContent(id).subscribe(
        (data: string) => {
          this.fileContent = data || 'Conteúdo não disponível';
          this.isLoadingContent = false;
        },
        (error: any) => {
          console.error('Erro ao buscar conteúdo do arquivo:', error);
          this.fileContent = 'Erro ao carregar o conteúdo do arquivo.';
          this.isLoadingContent = false;
        }
      );
    }
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.fileContent = '';
    this.modalTitle = '';
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDENTE': return 'status-pending';
      case 'PROCESSADO': return 'status-processed';
      case 'ERRO': return 'status-error';
      default: return 'status-unknown';
    }
  }
}