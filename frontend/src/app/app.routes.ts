import { Routes } from '@angular/router';
import { JobsListComponent } from './jobs-list/jobs-list';
import { JobDetailsComponent } from './job-details/job-details';

export const routes: Routes = [
  { path: 'jobs', component: JobsListComponent },
  { path: 'jobs/details/:id', component: JobDetailsComponent },
  { path: '', redirectTo: '/jobs', pathMatch: 'full' },
  { path: '**', redirectTo: '/jobs' }
];