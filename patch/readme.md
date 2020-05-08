1. apply this testcase.patch to commons-dbcp source
2. execute following command, and check error message 
  - command
    ```
    mvn test -Dtest=TestBasicDataSource2#testEvicter2
    ```
  - error message
    ```
    [ERROR] Failures: 
    [ERROR]   TestBasicDataSource2.testEvicter2:103->checkIfEvictorAlive:112 [EvictionTimer thread was destroyed with numIdle=8(expected: less or equal than 5)] 
    Expecting any elements of:
      <["process reaper",
        "surefire-forkedjvm-ping-30s",
        "surefire-forkedjvm-command-thread",
        "signal dispatcher",
        "finalizer",
        "reference handler",
        "main"]>
    to match given predicate but none did.
    ```

  testEvicter1: poolPreparedStatements = false
  testEvicter2: poolPreparedStatements = true
