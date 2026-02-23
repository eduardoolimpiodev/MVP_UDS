export interface User {
  id?: number;
  username: string;
  email: string;
  role: 'ADMIN' | 'USER';
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  role?: 'ADMIN' | 'USER';
}
