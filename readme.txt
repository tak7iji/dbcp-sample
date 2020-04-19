1. build
mvn clean package

2. execute
java -jar target/dbcp-sample-jar-with-dependencies.jar [true|false]
 true: set poolPreparedStatements to true
 false: setf poolPreparedStatements to false
