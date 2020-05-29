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
