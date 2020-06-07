package com.example;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
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
            for(int i=0; i< 20; i++) {
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
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(1000);
        ds.setMinEvictableIdleTimeMillis(100);
        ds.setValidationQuery("SELECT 1");
        ds.setPoolPreparedStatements(true);
        return ds;
    }

    public static void printDataSourceStats(DataSource ds) {
        BasicDataSource bds = (BasicDataSource) ds;
        System.out.println("NumActive: " + bds.getNumActive());
        System.out.println("NumIdle: " + bds.getNumIdle());
        
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] ids = threadBean.getAllThreadIds();
        ThreadInfo[] threads = threadBean.getThreadInfo(ids, 0);
        for (ThreadInfo info: threads) {
            System.out.println(info.getThreadName());
            if(info != null && info.getThreadName().toLowerCase().contains("evict")) {
                System.out.println(info.getThreadName() + ":" + info.getThreadId() + " is alive.");
            }
        }        
    }

    public static void shutdownDataSource(DataSource ds) throws SQLException {
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();
    }
}