FROM ubuntu:latest
LABEL authors="jeanc"
COPY target/BudgetManagement-0.0.1-SNAPSHOT.jar BudgetManagement-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["top", "-b"]