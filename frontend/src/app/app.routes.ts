import { Routes } from '@angular/router';
import { JobsListComponent } from './jobs-list/jobs-list';
import { JobFormComponent } from './job-form/job-form';
import { JobDetailsComponent } from './job-details/job-details';

export const routes: Routes = [
  { path: 'jobs', component: JobsListComponent },
  { path: 'jobs/new', component: JobFormComponent },
  { path: 'jobs/edit/:id', component: JobFormComponent },
  { path: 'jobs/details/:id', component: JobDetailsComponent },
  { path: '', redirectTo: '/jobs', pathMatch: 'full' },
  { path: '**', redirectTo: '/jobs' }
];