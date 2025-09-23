import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { JobService } from '../job';

@Component({
  selector: 'app-jobs-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './jobs-list.html',
  styleUrls: ['./jobs-list.css']
})
export class JobsListComponent implements OnInit {
  jobs: any[] = [];

  constructor(private jobService: JobService, private router: Router) {}

  ngOnInit(): void {
    this.getJobs();
  }

  getJobs(): void {
    this.jobService.getJobs().subscribe(
      (data) => {
        this.jobs = data;
      },
      (error) => {
        console.error('Erro ao buscar agendamentos:', error);
      }
    );
  }

  navigateToNewJob(): void {
    this.router.navigate(['/jobs/new']);
  }

  editJob(id: number): void {
    this.router.navigate(['/jobs/edit', id]);
  }

  viewDetails(id: number): void {
    this.router.navigate(['/jobs/details', id]);
  }

  deleteJob(id: number): void {
    if (confirm('Tem certeza que deseja excluir este agendamento?')) {
      this.jobService.deleteJob(id).subscribe(
        () => {
          this.getJobs(); // Atualiza a lista após a exclusão
        },
        (error) => {
          console.error('Erro ao excluir agendamento:', error);
        }
      );
    }
  }
}