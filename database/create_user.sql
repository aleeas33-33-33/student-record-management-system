-- Optional: dedicated application user (run as root after schema.sql)
CREATE USER IF NOT EXISTS 'srms_user'@'localhost' IDENTIFIED BY 'srms@123';
CREATE USER IF NOT EXISTS 'srms_user'@'%' IDENTIFIED BY 'srms@123';
GRANT ALL PRIVILEGES ON srms_db.* TO 'srms_user'@'localhost';
GRANT ALL PRIVILEGES ON srms_db.* TO 'srms_user'@'%';
FLUSH PRIVILEGES;
