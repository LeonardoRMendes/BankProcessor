import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobService } from '../job';

@Component({
  selector: 'app-job-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './job-form.html',
  styleUrls: ['./job-form.css']
})
export class JobFormComponent implements OnInit {
  job: any = { nome: '', cronExpression: '' };
  isEditMode: boolean = false;
  formSubmitted: boolean = false;
  isValidCron: boolean = true;
  errorMessage: string = '';

  constructor(
    private jobService: JobService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.jobService.getJobById(Number(id)).subscribe(
        (data) => {
          this.job = data;
        },
        (error) => {
          this.errorMessage = 'Agendamento não encontrado.';
        }
      );
    }
  }

  validateCron(cron: string): boolean {
    // Implemente uma validação de CRON expression.
    // Para simplificar, esta é uma validação básica. Recomenda-se usar uma biblioteca, como 'cron-validator'.
    const cronRegex = /^(\S+\s+\S+\s+\S+\s+\S+\s+\S+\s+\S+)$/;
    return cronRegex.test(cron);
  }

  saveJob(): void {
    this.formSubmitted = true;
    this.isValidCron = this.validateCron(this.job.cronExpression);
    this.errorMessage = '';

    if (!this.job.nome || !this.isValidCron) {
      this.errorMessage = 'Por favor, corrija os campos inválidos.';
      return;
    }

    if (this.isEditMode) {
      this.jobService.updateJob(this.job.id, this.job).subscribe(
        () => {
          this.router.navigate(['/jobs']);
        },
        (error) => {
          this.errorMessage = 'Erro ao atualizar agendamento.';
          console.error('Erro de atualização:', error);
        }
      );
    } else {
      this.jobService.createJob(this.job).subscribe(
        () => {
          this.router.navigate(['/jobs']);
        },
        (error) => {
          this.errorMessage = 'Erro ao criar agendamento.';
          console.error('Erro de criação:', error);
        }
      );
    }
  }

  cancel(): void {
    this.router.navigate(['/jobs']);
  }
}