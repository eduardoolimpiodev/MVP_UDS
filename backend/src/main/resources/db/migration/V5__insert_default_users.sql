-- Senha padrão para ambos os usuários: "password123"
-- Hash BCrypt gerado com strength 10

INSERT INTO users (username, password, email, role, created_at) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@ged.com', 'ADMIN', CURRENT_TIMESTAMP),
('user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user@ged.com', 'USER', CURRENT_TIMESTAMP);
