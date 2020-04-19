package com.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;

public class AppMain {

    public static void main(String[] args) {
        Connection conn = null;

        try {
            DataSource dataSource = setupDataSource("jdbc:h2:~/test");

            System.out.println("Creating connection.");
            conn = dataSource.getConnection();
            for(int i=0; i< 10; i++) {
                printDataSourceStats(dataSource);
                Thread.sleep(1000);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
    }

    public static DataSource setupDataSource(String connectURI) throws Exception {
        BasicDataSource ds = BasicDataSourceFactory.createDataSource(new Properties());
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl(connectURI);
        ds.setInitialSize(10);
        ds.setMaxIdle(10);
        ds.setMaxTotal(10);
        ds.setMinIdle(5);
        ds.setNumTestsPerEvictionRun(2);
        // ds.setTestOnBorrow(true);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(1000);
        ds.setMinEvictableIdleTimeMillis(100);
        ds.setValidationQuery("SELECT 1");
        // ds.setRemoveAbandonedOnBorrow(true);
        // ds.setLogAbandoned(true);
        ds.setPoolPreparedStatements(true);
        return ds;
    }

    public static void printDataSourceStats(DataSource ds) {
        BasicDataSource bds = (BasicDataSource) ds;
        System.out.println("NumActive: " + bds.getNumActive());
        System.out.println("NumIdle: " + bds.getNumIdle());
    }

    public static void shutdownDataSource(DataSource ds) throws SQLException {
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();
    }
}