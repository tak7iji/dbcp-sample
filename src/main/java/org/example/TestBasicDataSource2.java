package org.example;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.assertj.core.api.Assertions.*;

public class TestBasicDataSource2 {
    Logger logger = LogManager.getLogger(TestBasicDataSource2.class);
    protected BasicDataSource ds = null;
    protected ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    public static void main(String[] args) throws Exception {
        TestBasicDataSource2 test = new TestBasicDataSource2();
        test.setUp();
        test.testEvicter1();
        test.tearDown();

        test.setUp();
        test.testEvicter2();
        test.tearDown();
    }

    public void setUp() throws Exception {
        ds = createDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:~/test");
        ds.setValidationQuery("SELECT 1");
    }

    public void tearDown() throws Exception {
        ds.close();
        ds = null;
    }

    protected BasicDataSource createDataSource() throws Exception {
        return new BasicDataSource();
    }

    protected Connection getConnection() throws Exception {
        return ds.getConnection();
    }

    protected void setAdditionalProps() {
        ds.setInitialSize(10);
        ds.setMaxIdle(10);
        ds.setMaxTotal(10);
        ds.setMinIdle(5);
        ds.setNumTestsPerEvictionRun(1);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(1000);
        ds.setMinEvictableIdleTimeMillis(100);
    }

    protected void testEvicter1() throws Exception {
        setAdditionalProps();
        ds.setPoolPreparedStatements(false);
        checkIfEvictorAlive(false);
    }

    protected void testEvicter2() throws Exception {
        setAdditionalProps();
        ds.setPoolPreparedStatements(true);
        checkIfEvictorAlive(true);
    }

    protected void checkIfEvictorAlive(boolean flag) {
        try (final Connection conn = getConnection()) {
            logger.info("Start test(poolPreparedStatements = {}})", flag);
            for(int i=0; i< 20; i++) {
                assertThat(threadBean.getThreadInfo(threadBean.getAllThreadIds(), 0))
                    .as("EvictionTimer thread was destroyed with numIdle=%d(expected: less or equal than %d)", ds.getNumIdle(), ds.getMinIdle())
                    .extracting(t -> t.getThreadName().toLowerCase())
                    .anyMatch(s->s.contains("evict"));
                if (ds.getNumIdle() <= ds.getMinIdle()) break;
                Thread.sleep(1000);
            }
            logger.info("End test(numIdle={})",ds.getNumIdle());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}