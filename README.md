# testing-with-docker
Used to upload project examples of unit testing and integration testing with java and docker

## Project Structure
### Services:
Zombificator. Service designed to convert into a Zombie a character of Springfield Town.
It could be donde in group by lastname or individually by id.

### Components
SQLManager, class to interact with the Database.

### Tests
SQLManagert -> Testing for the services, it uses jUnit
SQLManagerIntegrationById -> Testing of the service in a concurrent way
SQLManagerIntegrationByLastname ->vTesting of the service in a concurrent way

### Resources
Queries to create tables and populate them.

### Dependencies
jUnit
MariaDB
TestContainers
Gradle
Java

## Usage
You could configure the project with your IDE or from the terminal with the `gradle` command.

## Known Issues

org.testcontainers.containers.ContainerLaunchException: Container startup failed
	at org.testcontainers.containers.GenericContainer.doStart(GenericContainer.java:330)
	at org.testcontainers.containers.GenericContainer.start(GenericContainer.java:311)

Caused by: org.testcontainers.containers.ContainerFetchException: Can't get Docker image: RemoteDockerImage(imageName=mariadb:10.1.41, imagePullPolicy=DefaultPullPolicy())
	at org.testcontainers.containers.GenericContainer.getDockerImageName(GenericContainer.java:1279)
	at org.testcontainers.containers.GenericContainer.logger(GenericContainer.java:613)
	at org.testcontainers.containers.GenericContainer.doStart(GenericContainer.java:320)
	... 26 more
Caused by: java.lang.IllegalStateException: Could not find a valid Docker environment. Please see logs and check configuration
	at org.testcontainers.dockerclient.DockerClientProviderStrategy.lambda$getFirstValidStrategy$3(DockerClientProviderStrategy.java:163)
	at java.base/java.util.Optional.orElseThrow(Optional.java:401)
	at org.testcontainers.dockerclient.DockerClientProviderStrategy.getFirstValidStrategy(DockerClientProviderStrategy.java:155)

**Solution: Start your docker env**

