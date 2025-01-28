FROM amazoncorretto:17-alpine
LABEL author="Kevin Britten & Jean Carlos Guzman"
COPY target/BudgetManagement-0.0.1-SNAPSHOT.jar BudgetManagement-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "BudgetManagement-0.0.1-SNAPSHOT.jar"]