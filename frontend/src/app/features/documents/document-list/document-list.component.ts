import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DocumentService } from '../../../core/services/document.service';
import { AuthService } from '../../../core/services/auth.service';
import { Document, DocumentStatus } from '../../../core/models/document.model';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './document-list.component.html',
  styleUrls: ['./document-list.component.scss']
})
export class DocumentListComponent implements OnInit {
  private documentService = inject(DocumentService);
  private authService = inject(AuthService);
  private router = inject(Router);

  documents: Document[] = [];
  loading = false;
  errorMessage = '';

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  searchTitle = '';
  filterStatus: DocumentStatus | '' = '';
  
  DocumentStatus = DocumentStatus;

  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.loading = true;
    this.errorMessage = '';

    const status = this.filterStatus || undefined;
    const title = this.searchTitle || undefined;

    this.documentService.getDocuments(
      this.currentPage,
      this.pageSize,
      title,
      status as DocumentStatus
    ).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.documents = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
        }
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to load documents';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadDocuments();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadDocuments();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadDocuments();
  }

  viewDocument(id: number): void {
    this.router.navigate(['/documents', id]);
  }

  logout(): void {
    this.authService.logout();
  }

  get currentUser() {
    return this.authService.getCurrentUser();
  }

  get isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  getStatusClass(status: DocumentStatus): string {
    return `badge-${status.toLowerCase()}`;
  }
}
