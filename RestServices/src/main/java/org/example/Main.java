package org.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
@ComponentScan({"model", "repository"})
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean(name = "props")
    @Primary
    public Properties getBdProperties() {
        Properties props = new Properties();
        try {
            System.out.println("Searching bd.config in directory " + ((new File(".")).getAbsolutePath()));
            File configFile = new File("bd.config");
            if (configFile.exists()) {
                props.load(new FileReader(configFile));
            } else {
                System.err.println("Configuration file bd.config not found");
            }
        } catch (IOException e) {
            System.err.println("Error reading configuration file bd.config: " + e);
        }
        return props;
    }

    @Bean
    public DataSource dataSource(@Qualifier("props")Properties props) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.user");
        String password = props.getProperty("jdbc.pass");

        if (url == null || username == null || password == null) {
            throw new IllegalStateException("Database properties not set correctly");
        }

        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
