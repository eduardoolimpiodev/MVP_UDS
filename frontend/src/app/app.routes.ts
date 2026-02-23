import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'documents',
    loadComponent: () => import('./features/documents/document-list/document-list.component').then(m => m.DocumentListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'documents/new',
    loadComponent: () => import('./features/documents/document-create/document-create.component').then(m => m.DocumentCreateComponent),
    canActivate: [authGuard]
  },
  {
    path: 'documents/:id',
    loadComponent: () => import('./features/documents/document-detail/document-detail.component').then(m => m.DocumentDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: '/documents',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/documents'
  }
];
