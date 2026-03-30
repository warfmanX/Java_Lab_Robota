-- 1. Таблица Уязвимостей
CREATE TABLE vulnerabilities (
    id BIGSERIAL PRIMARY KEY,
    title TEXT,
    description TEXT,
    severity_score DECIMAL(38, 2), -- БЫЛО: DOUBLE PRECISION. СТАЛО: DECIMAL (под BigDecimal)
    published_date DATE
);

-- 2. Таблица Серверов
CREATE TABLE system_assets (
    id BIGSERIAL PRIMARY KEY,
    hostname VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255),
    os VARCHAR(255),
    importance_level VARCHAR(50)
);

-- 3. Таблица Отчетов
CREATE TABLE scan_reports (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP,
    status VARCHAR(50),
    asset_id BIGINT REFERENCES system_assets(id)
);

-- 4. Связующая таблица
CREATE TABLE scan_report_vulnerabilities (
    scan_report_id BIGINT NOT NULL REFERENCES scan_reports(id),
    vulnerability_id BIGINT NOT NULL REFERENCES vulnerabilities(id),
    PRIMARY KEY (scan_report_id, vulnerability_id)
);