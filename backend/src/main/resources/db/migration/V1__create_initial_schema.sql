-- Ativar extensão para geração de UUID caso não esteja ativa (PostgreSQL nativo)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. TABELA DE USUÁRIOS
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(30) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT chk_user_role CHECK (role IN ('ROLE_ADMIN', 'ROLE_SUPPORT', 'ROLE_CUSTOMER'))
);

-- Index para otimizar a busca por e-mail no momento do login
CREATE INDEX idx_users_email ON users(email);

-- 2. TABELA DE CHAMADOS (TICKETS)
CREATE TABLE tickets (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         title VARCHAR(150) NOT NULL,
                         description TEXT NOT NULL,
                         status VARCHAR(30) NOT NULL,
                         priority VARCHAR(30) NOT NULL,
                         customer_id UUID NOT NULL,
                         assignee_id UUID,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_tickets_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT,
                         CONSTRAINT fk_tickets_assignee FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL,
                         CONSTRAINT chk_ticket_status CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
                         CONSTRAINT chk_ticket_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

-- Índices para paginação e filtros no Dashboard
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_customer ON tickets(customer_id);
CREATE INDEX idx_tickets_assignee ON tickets(assignee_id);

-- 3. TABELA DE COMENTÁRIOS (INTERAÇÕES DO CHAMADO)
CREATE TABLE ticket_comments (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 ticket_id UUID NOT NULL,
                                 user_id UUID NOT NULL,
                                 content TEXT NOT NULL,
                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_comments_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_comments_ticket ON ticket_comments(ticket_id);

-- 4. TABELA DE AUDITORIA/HISTÓRICO DE ALTERAÇÕES
CREATE TABLE ticket_history (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                ticket_id UUID NOT NULL,
                                changed_by UUID NOT NULL,
                                field_changed VARCHAR(50) NOT NULL,
                                old_value TEXT,
                                new_value TEXT,
                                changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_history_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
                                CONSTRAINT fk_history_user FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_history_ticket ON ticket_history(ticket_id);