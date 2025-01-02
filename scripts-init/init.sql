CREATE DATABASE product_manager_db;
CREATE USER product_manager_user WITH PASSWORD 'product_manager_password';
GRANT ALL PRIVILEGES ON DATABASE product_manager_db TO product_manager_user;
\c product_manager_db
GRANT ALL ON SCHEMA public TO product_manager_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO product_manager_user;


CREATE DATABASE checklist_db;
CREATE USER checklist_user WITH PASSWORD 'checklist_password';
GRANT ALL PRIVILEGES ON DATABASE checklist_db TO checklist_user;
\c checklist_db
GRANT ALL ON SCHEMA public TO checklist_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO checklist_user;


CREATE DATABASE limit_process_db;
CREATE USER limit_process_user WITH PASSWORD 'limit_process_password';
GRANT ALL PRIVILEGES ON DATABASE limit_process_db TO limit_process_user;
\c limit_process_db
GRANT ALL ON SCHEMA public TO limit_process_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO limit_process_user;
