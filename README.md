[![Build Status](https://travis-ci.org/USGS-CIDA/MLR-Legacy-Service.svg?branch=master)](https://travis-ci.org/USGS-CIDA/MLR-Legacy-Service) [![Coverage Status](https://coveralls.io/repos/github/USGS-CIDA/MLR-Legacy-Service/badge.svg?branch=master)](https://coveralls.io/github/USGS-CIDA/MLR-Legacy-Service?branch=master)

# MLR-Legacy-Service
Monitoring Location Legacy CRU Service

## Local Configuration
You will need a postgreSQL database to run this application. A Dockerized version is available at https://github.com/USGS-CIDA/MLR_Legacy_DB.
The configuration is located in src/main/results/application.yml. You will need to create an application.yml file in your local project's root directory to provide the variable values. It should contain:

```
mlrLegacyDbHost: localhost
mlrLegacyDbPort: 5435
mlrLegacyDataUsername: mlr_legacy_data
mlrLegacyDataPassword: changeMe

oauthResourceTokenKeyUri: https://your.auth.server.url/oauth/token_key
oauthResourceId: myResourceId

springFrameworkLogLevel: info

maintenanceRoles: ROLE_ONE, ROLE_TWO

keystoreLocation: classpath:yourKeystore.jks
keystorePassword: changeMe
```

Alternatively, you can take advantage of the [mlr-local-dev](https://github.com/USGS-CIDA/mlr-local-dev) convenience package to bring up mlr-legacy-db.

## Automated Testing
This application has two flavors of automated tests: unit tests (in the gov.usgs.wma.mlrlegacy package) and integration tests (in the gov.usgs.wma.mlrlegacy.db package) requiring a database. The unit tests can be run in isolation according to your normal practices.
The integration tests can be run in a terminal with the maven command ```mvn verify -P it``` in the project's root directory. Running in this manner will pull the database Docker image from the central repository and run it in a container.
They can also be run in your IDE against a database accessible to you. (Note that you should not use a shared database as the tests will destroy data and may have contention issues with other processes accessing the database.)
In either case, configuration information will be pulled from the maven setting.xml file. It will need to contain the following profile:
```
  <profile>
    <id>it</id>
    <properties>
      <postgresPassword>changeMe</postgresPassword>
      <mlrLegacyPassword>changeMe</mlrLegacyPassword>
      <mlrLegacyDataPassword>changeMe</mlrLegacyDataPassword>
      <mlrLegacyUserPassword>changeMe</mlrLegacyUserPassword>
      <mlrLegacyDataUsername>mlr_legacy_data</mlrLegacyDataUsername>
      <mlrLegacyServicePassword>changeMe</mlrLegacyServicePassword>
    </properties>
  </profile>
```

## Running the Application

This application can be run locally using the docker container built during the build process or by directly building and running the application JAR. The included `docker-compose` file has 3 profiles to choose from when running the application locally:

1. mlr-legacy: This is the default profile which runs the application as it would be in our cloud environment. This is not recommended for local development as it makes configuring connections to other services running locally on your machine more difficult.
2. mlr-legacy-local-dev: This is the profile which runs the application as it would be in the aqcu-local-dev project, and is configured to make it easy to replace the mlr-legacy instance in the local-dev project with this instance. It is run the same as the `mlr-legacy` profile, except it uses the docker host network driver.
3. mlr-notification-service-debug: This is the profile which runs the application exactly the same as `mlr-legacy-local-dev` but also enables remote debugging for the application and opens up port 8000 into the container for that purpose.

Before any of these options are able to be run you must also generate certificates for this application to serve using the `create_certificates` script in the `docker/certificates` directory. Additionally, this service must be able to connect to a running instance of Water Auth when starting, and it is recommended that you use the Water Auth instance from the `mlr-local-dev` project to accomplish this. In order for this application to communicate with any downstream services that it must call, including Water Auth, you must also place the certificates that are being served by those services into the `docker/certificates/import_certs` directory to be imported into the Java TrustStore of the running container.

To build and run the application after completing the above steps you can run: `docker-compose up --build {profile}`, replacing `{profile}` with one of the options listed above.
