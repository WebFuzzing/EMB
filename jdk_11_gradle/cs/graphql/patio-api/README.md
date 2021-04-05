[![Travis](https://travis-ci.org/patio-team/patio-api.svg?branch=development)](https://travis-ci.org/github/patio-team/patio-api) [![License](https://img.shields.io/github/license/patio-team/patio-api)](https://www.gnu.org/licenses/gpl-3.0.en.html)

# ![patio](etc/site/imgs/patio.png)

**patio** is a web application that tries to measure the happiness of a given team periodically by
asking for a level of happiness between Very bad and Very Good (being Very bad the saddest scenario and Very good the happiest). This repository
hosts the backend of the patio project. Cool!

![one](etc/site/imgs/one_50.png) ![two](etc/site/imgs/two_50.png) ![one](etc/site/imgs/three_50.png ) ![one](etc/site/imgs/four_50.png) ![one](etc/site/imgs/five_50.png)
## Getting Started

If you'd like to start working on the project. The following sections show you how to
do it.

### Requirements

What things you need to run the software:

* [Docker](https://docs.docker.com/)
* [Docker Compose](https://docs.docker.com/compose/)
* [JDK 11](https://openjdk.java.net/)

To install JDK11 you can also use [SDKMAN](https://sdkman.io/) a great tool to manage JVM related SDKs.

### Building

To build the code just execute the task **build**:

```shell
./gradlew build
```

Building the code implies running:

- static analysis
- code style checking
- test execution
- code coverage generation


If you'd like to enable continuous build execute:

```shell
./gradlew build -t
```

Continuous build means that Gradle does not exit and will re-execute tasks
when task file inputs change

### Running the tests

To execute only tests do:

```shell
./gradlew test
```

If you'd like to test a specific test or set of tests you can use the **--tests** option to
match the name of the test class/es you want to execute:

```shell
./gradlew test --tests *Repository*
```

There are more ways of executing tests, you can
take a look at this [blog entry](https://blog.jdriven.com/2017/10/run-one-or-exclude-one-test-with-gradle/)

### Running style checking

The project uses [Spotless](https://github.com/diffplug/spotless) for code style checking.
To make sure contributors follow the style rules the project uses :

```shell
./gradlew spotlessJavaCheck
```

If you find any error you can format the conflicted files automatically by
executing:

```shell
./gradlew spotlessJavaApply
```

### Running missing license headers

To make sure any new file added to `src` or `etc` have the license header, run:

```shell
./gradlew license --rerun-tasks
```

If any error arises you can apply the license header just executing:

```shell
./gradlew licenseFormat --rerun-tasks
```

Applying the `--rerun-tasks` is because sometimes the task result is cached and may lead to
unwanted situations.

### Running static analysis

The project uses [PMD](https://pmd.github.io/) as static analysis checker. To execute **PMD** for main classes do:

```shell
./gradlew pmdMain
```

And for test classes:

```shell
./gradlew pmdTest
```

### Running bug finding

This project uses [Spotbugs](https://github.com/spotbugs) as tool for spotting possible new bugs. To execute
**Spotbugs** just execute:

```shell
./gradlew spotbugsMain
```

### Running dev environment

#### Running services

When working on development we'll need a PostgreSQL database and sometimes the front end. There's a
`docker-compose` file to bootstrap both systems and make them work with the current back development.

Go  to **your project's** `etc/docker` (patio-api/etc/docker) folder and execute:

`docker-compose up -d`

#### Configuration

Before running the API you may want to configure some properties. By default the application 
looks for the configuration file `application.yml`. That file doesn't exists by default, but there's
the `application.yml.template` that you can copy to `application.yml` and change whatever you want there.

When in production environment, there's the `application-prod.yml` that uses environment variables to
complete configuration properties values. 

##### Database

Configuration file:

```yaml
datasources:
  default:
    url: ${PATIO_JDBC_URL}
    username: ${PATIO_JDBC_USER}
    password: ${PATIO_JDBC_PASSWORD}
    driverClassName: ${PATIO_JDBC_DRIVER}
```

And environment variables

| Name                       | Description                 | Default value                                            |
|:---------------------------|:----------------------------|:---------------------------------------------------------|
| PATIO_JDBC_URL             | JDBC url                   | jdbc:postgresql://localhost:5433/patio                    |
| PATIO_JDBC_USER            | JDBC username              | patio                                                     |
| PATIO_JDBC_PASSWOR         | JDBC password              | patio                                                     |
| PATIO_JDBC_DRIVER          | JDBC driver                | org.postgresql.Driver                                     |

##### AWS integration

Configuration file section:

```yaml
aws:
  credentials:
    accessKey: ${PATIO_AWS_ACCESS_KEY}
    secretKey: ${PATIO_AWS_SECRET_KEY}
  mail:
    sourceemail: ${PATIO_AWS_EMAIL_SOURCE}
    region: ${PATIO_AWS_EMAIL_REGION}
    enabled: ${PATIO_AWS_EMAIL_ENABLED}
```

And environment variables:

| Name                        | Description                 | Default value                                                 |
|:----------------------------|:----------------------------|:--------------------------------------------------------------|
| PATIO_ACCESS_KEY            | AWS access key              |                                                               |
| PATIO_SECRET_KEY            | AWS secret key              |                                                               |
| PATIO_AWS_EMAIL_SOURCE      | AWS source email            |                                                               |
| PATIO_AWS_EMAIL_REGION      | AWS region                  |                                                               |
| PATIO_AWS_EMAIL_ENABLED     | Enable AWS mailing          |                                                               |

##### JWT

Configuration file section:

```yaml 
crypto:
  password: SHA-256
  jwt:
    secret: ${PATIO_JWT_SECRET}
    days: ${PATIO_JWT_DAYS}
    algorithm: ${PATIO_JWT_ALGO}
    issuer: ${PATIO_JWT_ISSUER}
```

And environment variables:

| Name                       | Description                 | Default value                                                 |
|:---------------------------|:----------------------------|:--------------------------------------------------------------|
| PATIO_JWT_SECRET           | JWT secret                  | mysupersecret                                                 |
| PATIO_JWT_ALGO             | JWT signature algorithm     | HS256                                                         |
| PATIO_JWT_ISSUER           | JWT issuer claim            | patio                                                         |
| PATIO_JWT_DAYS             | JWT days before out of date | 7                                                             |

##### GOOGLE-OAUTH2

Configuration file section:

```yaml
oauth2:
  apikey: ${PATIO_OAUTH2_KEY}
  apisecret: ${PATIO_OAUTH2_SECRET}
  callback: ${PATIO_OAUTH2_CALLBACK}
```

Google Oauth2 settings are required if you want front end to be authenticated using Google authentication.

| Name                        | Description                 | Default value                                                 |
|:----------------------------|:----------------------------|:--------------------------------------------------------------|
| PATIO_OAUTH2_KEY            | Oauth2 client id            |                                                               |
| PATIO_OAUTH2_SECRET         | Oauth2 client secret        |                                                               |
| PATIO_OAUTH2_CALLBACK       | Oauth2 callback URL         |                                                               |

`PATIO_OAUTH2_CALLBACK` must match frontend `VUE_APP_REDIRECT_URI` variable.

##### DEFAULT USER

Configuration file section:

```yaml
duser:
  enabled: ${PATIO_DUSER_ENABLED}
  name: ${PATIO_DUSER_NAME}
  email: ${PATIO_DUSER_EMAIL}
  password: ${PATIO_DUSER_PASSWORD}
```

In case you'd like to start the instance with a default user you can use the following environment variables:

| Name                       | Description                           | Default value                                        |
|:---------------------------|:--------------------------------------|:-----------------------------------------------------|
| PATIO_DUSER_ENABLED        | Whether or not to insert default user | false                                                |
| PATIO_DUSER_NAME           | Default user's name                   |                                                      |
| PATIO_DUSER_EMAIL          | Default user's email                  |                                                      |
| PATIO_DUSER_PASSWORD       | Default user's plain text password    |                                                      | 

#### Running app

Both the database and the front end will be installed and bootstrapped. Now you can execute:

```shell
./gradlew run
```

And the api will use the database register in the docker-compose.yml.

**IMPORTANT**: Remember that the database is using a docker volume, if you would like to get rid of database data
you can do it by executing:

```shell
docker volume rm NAME-OF-THE-VOLUME
```

### Loading Fixtures

If it's the first time you are running the application, you may want to load some fixtures
to the database first. There're a couple of Gradle tasks to
load/clean project's data fixtures:

- fixtures-load
- fixtures-clean

`fixtures-load` loops over the sql files in 'fixtures/load' and executes them
against the database configured in `fixtures/fixtures.yaml`. The order of
execution depends on the file name.

```shell
./gradlew fixtures-load
```

`fixtures-clean` loops over the sql files in 'fixtures/clean' and executes them
against the database configured in `fixtures/fixtures.yaml`. The order of
execution also depends on the file name.

```shell
./gradlew fixtures-clean
```

You can change where the load/clean files are as well as the database configuration
file. In your **build.gradle** you can use the fixtures extension:

```groovy
fixtures {
  cleanDir '...'
  loadDir '...'
  configFile '...'
}
```

## Technologies

The most important technologies you need to be aware of to work
on this project are:

### Runtime

* [Micronaut](https://micronaut.io/) - A JVM-based micro-framework
* [Jooq](https://www.jooq.org/) - Persistence framework
* [AWS API](https://aws.amazon.com/sdk-for-java/) - For AWS services (e.g. mailing)

### Testing

* [JUnit 5](https://junit.org/junit5/) - Java testing library
* [Test containers](https://www.testcontainers.org/) For testing integrations

### Building

* [Gradle](https://gradle.org/) - Building tool

## How to contribute

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.

## License

https://www.gnu.org/licenses/gpl-3.0.html

## Acknowledgments

Thanks to [Kaleidos Open Source](https://kaleidos.net/) to make this project possible.