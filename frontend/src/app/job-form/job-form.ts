import { Component, OnInit, OnChanges, SimpleChanges, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobService } from '../job.service';

@Component({
  selector: 'app-job-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './job-form.html',
  styleUrls: ['./job-form.css']
})
export class JobFormComponent implements OnInit, OnChanges {
  @Input() jobToEdit: any = null;
  @Output() jobSaved = new EventEmitter<void>();
  @Output() formCanceled = new EventEmitter<void>();

  job: any = { nome: '', cronExpression: '' };
  isEditMode: boolean = false;
  formSubmitted: boolean = false;
  isValidCron: boolean = true;
  errorMessage: string = '';

  constructor(private jobService: JobService) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['jobToEdit']) {
      this.initializeForm();
    }
  }

  private initializeForm(): void {
    if (this.jobToEdit) {
      this.isEditMode = true;
      this.job = { ...this.jobToEdit };
    } else {
      this.isEditMode = false;
      this.job = { nome: '', cronExpression: '' };
    }

    this.formSubmitted = false;
    this.isValidCron = true;
    this.errorMessage = '';
  }

  validateCron(cronExpression: string): boolean {
    if (!cronExpression || cronExpression.trim() === '') {
      return false;
    }
    
    const trimmed = cronExpression.trim();
    const fields = trimmed.split(/\s+/);
    
    if (fields.length !== 5 && fields.length !== 6) {
      return false;
    }
    
    const cronPattern = /^(\*|[0-5]?\d|\?|\/|\-|,)+\s+(\*|[01]?\d|2[0-3]|\?|\/|\-|,)+\s+(\*|[0-2]?\d|3[01]|\?|\/|\-|,|L|W)+\s+(\*|[01]?\d|\?|\/|\-|,|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)+\s+(\*|[0-6]|\?|\/|\-|,|SUN|MON|TUE|WED|THU|FRI|SAT|L|#)+(\s+(\*|19[7-9]\d|20\d{2}|\?|\/|\-|,)+)?$/i;
    
    return cronPattern.test(trimmed);
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
          this.jobSaved.emit();
        },
        (error: any) => {
          this.errorMessage = 'Erro ao atualizar agendamento.';
          console.error('Erro de atualização:', error);
        }
      );
    } else {
      this.jobService.createJob(this.job).subscribe(
        () => {
          this.jobSaved.emit();
        },
        (error: any) => {
          this.errorMessage = 'Erro ao criar agendamento.';
          console.error('Erro de criação:', error);
        }
      );
    }
  }

  cancel(): void {
    this.formCanceled.emit();
  }
}