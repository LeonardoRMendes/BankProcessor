import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { JobService } from '../job.service';

@Component({
  selector: 'app-job-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './job-details.html',
  styleUrls: ['./job-details.css']
})
export class JobDetailsComponent implements OnInit {
  jobId: number | null = null;
  jobDetails: any = null;
  files: any[] = [];
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private jobService: JobService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.jobId = Number(id);
      this.getJobDetails(this.jobId);
      this.getJobFiles(this.jobId);
    } else {
      this.errorMessage = 'ID do agendamento não fornecido.';
    }
  }

  getJobDetails(id: number): void {
    this.jobService.getJobById(id).subscribe(
      (data: any) => {
        this.jobDetails = data;
      },
      (error: any) => {
        this.errorMessage = 'Agendamento não encontrado.';
      }
    );
  }

  getJobFiles(id: number): void {
    this.jobService.getJobFiles(id).subscribe(
      (data: any[]) => {
        this.files = data;
      },
      (error: any) => {
        this.errorMessage = 'Erro ao carregar arquivos do agendamento.';
      }
    );
  }

  goBack(): void {
    this.router.navigate(['/jobs']);
  }
}