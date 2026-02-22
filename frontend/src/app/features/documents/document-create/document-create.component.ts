import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { DocumentService } from '../../../core/services/document.service';
import { DocumentStatus } from '../../../core/models/document.model';

@Component({
  selector: 'app-document-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TranslateModule],
  templateUrl: './document-create.component.html',
  styleUrl: './document-create.component.scss'
})
export class DocumentCreateComponent {
  private fb = inject(FormBuilder);
  private documentService = inject(DocumentService);
  private router = inject(Router);

  documentForm: FormGroup;
  loading = false;
  error: string | null = null;

  statuses = Object.values(DocumentStatus);
  DocumentStatus = DocumentStatus;

  constructor() {
    this.documentForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: [''],
      tags: [''],
      status: [DocumentStatus.DRAFT, Validators.required]
    });
  }

  onSubmit(): void {
    if (this.documentForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;

    const formValue = this.documentForm.value;
    const tags = formValue.tags ? formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [];

    const documentRequest = {
      title: formValue.title,
      description: formValue.description || null,
      tags: tags,
      status: formValue.status
    };

    this.documentService.createDocument(documentRequest).subscribe({
      next: (response) => {
        if (response.success) {
          this.router.navigate(['/documents', response.data.id]);
        } else {
          this.error = response.message || 'Erro ao criar documento';
          this.loading = false;
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao criar documento';
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/documents']);
  }
}
