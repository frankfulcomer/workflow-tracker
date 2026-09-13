package com.example.tracker.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * Opt-in only (the "sql-verify" profile is never active by default): exposes
 * this app's in-memory H2 database over H2's own TCP wire protocol, bound to
 * localhost only, so a separate process (the QA SQL verification suite) can
 * open a second JDBC connection to the exact same in-memory database this
 * app instance is using. Does not change the datasource, schema, or any
 * other behavior for the default profile.
 */
@Configuration
@Profile("sql-verify")
public class H2TcpServerConfig {

    @Bean(destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer("-tcpPort", "9092", "-ifNotExists").start();
    }
}
