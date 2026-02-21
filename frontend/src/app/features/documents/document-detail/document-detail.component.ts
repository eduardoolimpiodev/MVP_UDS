import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DocumentService } from '../../../core/services/document.service';
import { Document, DocumentVersion } from '../../../core/models/document.model';

@Component({
  selector: 'app-document-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './document-detail.component.html',
  styleUrls: ['./document-detail.component.scss']
})
export class DocumentDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private documentService = inject(DocumentService);

  document: Document | null = null;
  versions: DocumentVersion[] = [];
  loading = false;
  errorMessage = '';
  uploadingFile = false;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadDocument(+id);
      this.loadVersions(+id);
    }
  }

  loadDocument(id: number): void {
    this.loading = true;
    this.documentService.getDocumentById(id).subscribe({
      next: (response) => {
        if (response.success) {
          this.document = response.data;
        }
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to load document';
        this.loading = false;
      }
    });
  }

  loadVersions(id: number): void {
    this.documentService.getVersions(id).subscribe({
      next: (response) => {
        if (response.success) {
          this.versions = response.data;
        }
      },
      error: (error) => {
        console.error('Failed to load versions:', error);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0 && this.document) {
      const file = input.files[0];
      this.uploadFile(file);
    }
  }

  uploadFile(file: File): void {
    if (!this.document) return;

    this.uploadingFile = true;
    this.errorMessage = '';

    this.documentService.uploadVersion(this.document.id, file).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadVersions(this.document!.id);
          this.loadDocument(this.document!.id);
        }
        this.uploadingFile = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to upload file';
        this.uploadingFile = false;
      }
    });
  }

  downloadFile(versionId: number, fileName: string): void {
    this.documentService.downloadFile(versionId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Failed to download file';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/documents']);
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }
}
