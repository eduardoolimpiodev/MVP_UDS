export enum DocumentStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED'
}

export interface Document {
  id: number;
  title: string;
  description?: string;
  tags: string[];
  ownerUsername: string;
  tenantId?: string;
  status: DocumentStatus;
  currentVersion?: number;
  createdAt: string;
  updatedAt: string;
}

export interface DocumentVersion {
  id: number;
  versionNumber: number;
  fileName: string;
  fileSize: number;
  mimeType: string;
  uploadedBy: string;
  uploadedAt: string;
}

export interface DocumentCreateRequest {
  title: string;
  description?: string;
  tags: string[];
  tenantId?: string;
}

export interface DocumentUpdateRequest {
  title?: string;
  description?: string;
  tags?: string[];
  tenantId?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp: string;
}
