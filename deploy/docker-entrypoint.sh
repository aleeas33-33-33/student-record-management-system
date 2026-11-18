#!/bin/bash
# Entry point for the SRMS container.
#  1. Points Tomcat's HTTP connector at $PORT (Render/Railway style hosting).
#  2. If DB_URL is NOT set: starts the embedded MariaDB and loads database/schema.sql.
#  3. Starts Tomcat in the foreground.
set -e

PORT="${PORT:-8080}"
sed -i "s/port=\"8080\"/port=\"${PORT}\"/" /usr/local/tomcat/conf/server.xml
echo "[srms] Tomcat HTTP port: ${PORT}"

if [ -z "${DB_URL}" ]; then
    echo "[srms] DB_URL not set -> starting embedded MariaDB"
    mkdir -p /run/mysqld && chown mysql:mysql /run/mysqld
    if [ ! -d /var/lib/mysql/mysql ]; then
        mariadb-install-db --user=mysql --datadir=/var/lib/mysql > /dev/null
    fi
    mysqld_safe --user=mysql > /var/log/mariadb-safe.log 2>&1 &
    for i in $(seq 1 60); do
        if mariadb-admin ping --silent 2>/dev/null; then break; fi
        sleep 1
    done
    if ! mariadb -e "USE srms_db" 2>/dev/null; then
        echo "[srms] Loading database/schema.sql (seed data)"
        mariadb < /opt/srms/database/schema.sql
        mariadb < /opt/srms/database/create_user.sql
    fi
    export DB_URL="jdbc:mysql://127.0.0.1:3306/srms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
    export DB_USER="srms_user"
    export DB_PASSWORD="srms@123"
else
    echo "[srms] Using external database from DB_URL"
fi

exec /usr/local/tomcat/bin/catalina.sh run
