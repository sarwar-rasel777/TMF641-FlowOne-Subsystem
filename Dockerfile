ARG AAX_VERSION=trunk
FROM repository.int.compax.at:5001/rp-spring-boot:${AAX_VERSION}

USER compax

COPY ./target/*.jar /tl-tmf641-flowone-subsystem.jar

EXPOSE 8080
ENTRYPOINT ["/vol1/entrypoint.sh", "/tl-tmf641-flowone-subsystem.jar"]
