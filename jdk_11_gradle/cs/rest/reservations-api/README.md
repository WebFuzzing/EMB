# Reservations API

Simple API built with SpringBoot and MongoDB database.

## Documentation

Documentation can be found [here](https://cg-reservations-api.herokuapp.com/documentation)

## How to run

### Application

1. Clone the repository by executing commands:

```
cd <yourRepoDirectory>
git clone https://github.com/cyrilgavala/reservations-api.git .
```

2. Open the project with your preferable IDE.
   If you use IntelliJ IDEA, it will offer you a **SpringBoot** runner configuration.
3. Update the runner by adding environment variable ```DATABASE_URL``` containing
   URL to your MongoDB database and ```JWT_SECRET``` with 512-bit secret.
4. Run the runner configuration.

### Tests

1. To run tests you need to pass step 2. from previous instructions and run command:

   ```./gradlew test```

   It will also execute ```jacocoTestReport``` gradle task, which will generate
   test report on path ```reservation-api/build/reports/jacoco/test/html/index.html```.
2. To run whether you pass 95% test coverage check, simply run command:

   ```./gradlew jacocoTestCoverageVerification```
