# =====================================================================
#  SRMS - single-container image for free hosting (e.g. Render free tier)
#  Tomcat 9 (javax) + the SRMS WAR + an embedded MariaDB seeded from
#  database/schema.sql.  If DB_URL is set, the embedded DB is skipped and
#  the app connects to that external MySQL instead.
# =====================================================================

# ---------- Stage 1: build the WAR with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY srms-web/pom.xml srms-web/pom.xml
RUN mvn -q -B -f srms-web/pom.xml dependency:go-offline
COPY srms-web/src srms-web/src
RUN mvn -q -B -f srms-web/pom.xml package -DskipTests

# ---------- Stage 2: runtime = Tomcat 9 + MariaDB ----------
FROM tomcat:9.0-jdk17-temurin-jammy
RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends mariadb-server mariadb-client \
 && rm -rf /var/lib/apt/lists/* \
 && rm -rf /usr/local/tomcat/webapps/*

COPY database/ /opt/srms/database/
COPY deploy/mariadb-small.cnf /etc/mysql/mariadb.conf.d/99-srms-small.cnf
COPY deploy/docker-entrypoint.sh /opt/srms/docker-entrypoint.sh
COPY --from=build /build/srms-web/target/srms.war /usr/local/tomcat/webapps/ROOT.war
RUN chmod +x /opt/srms/docker-entrypoint.sh

# Render injects PORT (default 10000); 8080 is used when PORT is not set.
ENV PORT=8080 \
    CATALINA_OPTS="-Xms64m -Xmx256m -XX:+UseSerialGC" \
    TZ=Asia/Kolkata
EXPOSE 8080
CMD ["/opt/srms/docker-entrypoint.sh"]
