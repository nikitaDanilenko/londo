FROM eclipse-temurin:25.0.3_9-jdk AS build

WORKDIR /app

RUN apt-get update && apt-get install -y curl bash && rm -rf /var/lib/apt/lists/*

RUN curl -sL https://raw.githubusercontent.com/dwijnand/sbt-extras/12394d5/sbt -o /usr/local/bin/sbt && \
    chmod +x /usr/local/bin/sbt

COPY project/build.properties project/
RUN sbt -v sbtVersion

COPY build.sbt ./
COPY project/*.sbt project/*.scala project/
COPY conf/ conf/

RUN sbt update

COPY app/ app/

RUN sbt stage

FROM eclipse-temurin:25.0.3_9-jre

WORKDIR /opt/docker

COPY --from=build /app/target/universal/stage/ /opt/docker/

RUN apt-get update && apt-get install -y bash && \
    chmod +x /opt/docker/bin/londo && \
    adduser --disabled-password --gecos "" --uid 1001 appuser

EXPOSE 9000

USER appuser

CMD ["/opt/docker/bin/londo", "-Dpidfile.path=/dev/null"]