# BuzMo Social Network

To compile, you should have Oracle JDK 8 installed and have `JAVA_HOME`
set to its directory. The `java` and `javac` commands should likewise be configured
for JDK 8 (Java 1.8).

To build `cd` to the repo directory then:

    mvn clean package

To run:

    java -jar target/BuzMo.jar server config/local.yaml

Try visiting `/api/hello` (the sample HelloResource) or `/admin/healthcheck` (part of
Dropwizard) or `/` (the frontend).

When running `add_demo_data.sql` from SQLPlus make sure to enter into the prompt:

    set define off

before running:

    @add_demo_data.sql


To setup and run the frontend:

    cd src/main/resources/frontend
    npm install
    npm run


