# Use maven to compile the java application.
FROM docker.io/maven:3.9.8-amazoncorretto-21 AS build

# Set the working directory to /app
WORKDIR /workspace/app

# copy project content
COPY . ./

# Compile the application.
RUN mvn clean package -DskipTests

# Unpack the fatjar to create application layers
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Build runtime image.
FROM azul/zulu-openjdk:21-jre

VOLUME /tmp

# Copy the application layers
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Starts java app from the entrypoint
ENTRYPOINT ["java", "-Duser.timezone=America/Santiago", "-cp", "app:app/lib/*", "app.alegon.olympicsdataloader.OlympicsDataLoaderApplication"]