CREATE DATABASE product_manager_db;
CREATE USER product_manager_user WITH PASSWORD 'product_manager_password';
GRANT ALL PRIVILEGES ON DATABASE product_manager_db TO product_manager_user;
\c product_manager_db
GRANT ALL ON SCHEMA public TO product_manager_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO product_manager_user;


CREATE DATABASE product_connector_db;
CREATE USER product_connector_user WITH PASSWORD 'product_connector_password';
GRANT ALL PRIVILEGES ON DATABASE product_connector_db TO product_connector_user;
\c product_connector_db
GRANT ALL ON SCHEMA public TO product_connector_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO product_connector_user;
