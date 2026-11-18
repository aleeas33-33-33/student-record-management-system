package com.srms.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Hibernate + DataSource configuration (Spring Dependency Injection).
 * DB_URL / DB_USER / DB_PASSWORD environment variables override db.properties,
 * so the same WAR can be deployed to any server.
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
public class HibernateConfig {

    private final Environment env;

    public HibernateConfig(Environment env) {
        this.env = env;
    }

    private String setting(String envName, String propName) {
        String v = System.getenv(envName);
        if (v == null || v.isBlank()) v = System.getProperty(envName);
        return (v == null || v.isBlank()) ? env.getProperty(propName) : v;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl(setting("DB_URL", "db.url"));
        cfg.setUsername(setting("DB_USER", "db.user"));
        cfg.setPassword(setting("DB_PASSWORD", "db.password"));
        cfg.setMaximumPoolSize(5);
        cfg.setPoolName("srms-pool");
        // Do not fail Tomcat start-up if the DB is temporarily unreachable
        cfg.setInitializationFailTimeout(-1);
        return new HikariDataSource(cfg);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sf = new LocalSessionFactoryBean();
        sf.setDataSource(dataSource);
        sf.setPackagesToScan("com.srms.model");
        Properties p = new Properties();
        p.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        p.put("hibernate.show_sql", env.getProperty("hibernate.show_sql", "false"));
        p.put("hibernate.format_sql", "true");
        p.put("hibernate.hbm2ddl.auto", "none");
        p.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        // Validation is done by Spring MVC (@Valid) in the controller layer
        p.put("javax.persistence.validation.mode", "none");
        sf.setHibernateProperties(p);
        return sf;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}
