import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Document, 
  DocumentCreateRequest, 
  DocumentUpdateRequest, 
  DocumentVersion, 
  PageResponse, 
  ApiResponse,
  DocumentStatus 
} from '../models/document.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/documents`;

  getDocuments(
    page: number = 0,
    size: number = 10,
    title?: string,
    status?: DocumentStatus,
    sortBy: string = 'createdAt',
    sortDirection: string = 'DESC'
  ): Observable<ApiResponse<PageResponse<Document>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    if (title) {
      params = params.set('title', title);
    }
    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<ApiResponse<PageResponse<Document>>>(this.apiUrl, { params });
  }

  getDocumentById(id: number): Observable<ApiResponse<Document>> {
    return this.http.get<ApiResponse<Document>>(`${this.apiUrl}/${id}`);
  }

  createDocument(document: DocumentCreateRequest): Observable<ApiResponse<Document>> {
    return this.http.post<ApiResponse<Document>>(this.apiUrl, document);
  }

  updateDocument(id: number, document: DocumentUpdateRequest): Observable<ApiResponse<Document>> {
    return this.http.put<ApiResponse<Document>>(`${this.apiUrl}/${id}`, document);
  }

  updateDocumentStatus(id: number, status: DocumentStatus): Observable<ApiResponse<Document>> {
    return this.http.patch<ApiResponse<Document>>(`${this.apiUrl}/${id}/status`, { status });
  }

  deleteDocument(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  uploadVersion(documentId: number, file: File): Observable<ApiResponse<DocumentVersion>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ApiResponse<DocumentVersion>>(`${this.apiUrl}/${documentId}/versions`, formData);
  }

  getVersions(documentId: number): Observable<ApiResponse<DocumentVersion[]>> {
    return this.http.get<ApiResponse<DocumentVersion[]>>(`${this.apiUrl}/${documentId}/versions`);
  }

  downloadFile(versionId: number): Observable<Blob> {
    return this.http.get(`${environment.apiUrl}/files/${versionId}`, {
      responseType: 'blob'
    });
  }
}
