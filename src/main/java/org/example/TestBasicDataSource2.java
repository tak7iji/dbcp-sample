package org.example;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.util.Arrays;

import org.apache.commons.dbcp2.BasicDataSource;

public class TestBasicDataSource2 {
    protected BasicDataSource ds = null;

    public static void main(String[] args) {
        TestBasicDataSource2 test = new TestBasicDataSource2();
        test.setUp();
    }

    public void setUp() {
        try {
            ds = createDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:~/test");
            ds.setValidationQuery("SELECT 1");

            testEvicter1();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected BasicDataSource createDataSource() throws Exception {
        return new BasicDataSource();
    }

    protected Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    protected void testEvicter1() throws Exception {
        ds.setInitialSize(10);
        ds.setMaxIdle(10);
        ds.setMaxTotal(10);
        ds.setMinIdle(5);
        ds.setNumTestsPerEvictionRun(2);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(1000);
        ds.setMinEvictableIdleTimeMillis(100);
        ds.setPoolPreparedStatements(Boolean.TRUE);

        try (final Connection conn = getConnection()) {

            for(int i=0; i< 20; i++) {
                if(!isEvictorAlive()) {
                    System.out.println("Evicter is disappeared");
                    break;
                }
                Thread.sleep(1000);
            }
            System.out.println("Num idle="+ds.getNumIdle());
        }
    }

    protected boolean isEvictorAlive() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] ids = threadBean.getAllThreadIds();
        ThreadInfo[] threads = threadBean.getThreadInfo(ids, 0);
        return Arrays.stream(threads).anyMatch(i -> i.getThreadName().toLowerCase().contains("evict"));
    }
}