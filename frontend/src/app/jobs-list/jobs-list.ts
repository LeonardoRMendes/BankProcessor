import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { JobService } from '../job.service';
import { ModalComponent } from '../modal/modal.component';
import { JobFormComponent } from '../job-form/job-form';

@Component({
  selector: 'app-jobs-list',
  standalone: true,
  imports: [CommonModule, ModalComponent, JobFormComponent],
  templateUrl: './jobs-list.html',
  styleUrls: ['./jobs-list.css']
})
export class JobsListComponent implements OnInit {
  jobs: any[] = [];
  isModalOpen = false;
  modalTitle = '';
  selectedJob: any = null;

  constructor(private jobService: JobService, private router: Router) {}

  ngOnInit(): void {
    this.getJobs();
  }

  getJobs(): void {
    this.jobService.getJobs().subscribe(
      (data: any[]) => {
        this.jobs = data;
      },
      (error: any) => {
        console.error('Erro ao buscar agendamentos:', error);
      }
    );
  }

  navigateToNewJob(): void {
    this.selectedJob = null;
    this.modalTitle = 'Novo Agendamento';
    this.isModalOpen = true;
  }

  navigateToArquivos(): void {
    this.router.navigate(['/arquivos-retorno']);
  }

  editJob(id: number): void {
    this.selectedJob = this.jobs.find(job => job.id === id);
    this.modalTitle = 'Editar Agendamento';
    this.isModalOpen = true;
  }

  viewDetails(id: number): void {
    this.router.navigate(['/jobs/details', id]);
  }

  onJobSaved(): void {
    this.isModalOpen = false;
    this.getJobs();
  }

  onFormCanceled(): void {
    this.isModalOpen = false;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  deleteJob(id: number): void {
    if (confirm('Tem certeza que deseja excluir este agendamento?')) {
      this.jobService.deleteJob(id).subscribe(
        () => {
          this.getJobs();
        },
        (error: any) => {
          console.error('Erro ao excluir agendamento:', error);
        }
      );
    }
  }
}