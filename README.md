# Gabor Dicso test task for Intland
This project is an implementation of a simple, tree based content management service as a test task for Intland.
## Requirements
You'll need Java 8 and Maven 3.x to build and run the project.
## Usage
### Build
Clone the project, cd to the `gabordicso-test` folder, and run `mvn clean package`. It will build the project, run the unit tests and integration tests, and package the project in a `.war` file.
#### Configuration
The path of the file in which to store the tree can be set in `src/main/resources/application.properties`. The name of the property is `gabordicsotest.treerepo.filePath`. Its default value is `tree.json`. If you wish to specify a different path, do so before building the project.
### Run
In the `gabordicso-test` folder, cd to `target` and run `java -jar gabordicso-test-0.0.1-SNAPSHOT.war`. Then open http://localhost:8080/ in your browser.
## Known problems
The project does not provide protection against CSRF attacks.
