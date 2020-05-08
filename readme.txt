1. build
mvn clean package

2. execute
java -jar target/dbcp-sample-jar-with-dependencies.jar

3. patch
 (1) clonse commons-dbcp
 (2) cd commons-dbcp directory 
 (3) patch -p1 < /path/to/patch/testcase.patch
