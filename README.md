# MLR-Legacy-Service

[![Build Status](https://travis-ci.org/USGS-CIDA/MLR-Legacy-Service.svg?branch=master)](https://travis-ci.org/USGS-CIDA/MLR-Legacy-Service) [![Coverage Status](https://coveralls.io/repos/github/USGS-CIDA/MLR-Legacy-Service/badge.svg?branch=master)](https://coveralls.io/github/USGS-CIDA/MLR-Legacy-Service?branch=master)
Monitoring Location Legacy CRU Service

## Service Description

This is one of the MLR microservices and is primarily responsible for any interactions with the MLR Database with regards to locations. This service is called a CRU service for Create, Read, Update because at this time the service _does not_ support deleting locations from the MLR Database. When a location is going to be added, updated, or retrieved from the MLR Database this should _only_ happen through the API methods of this service and no other service should have a direct connection the MLR Database (with the Gateway being a caveat explained in that service's readme).

When a monitoring location being persisted in the database (for an Add or an Update) this service also performs some database integrity validation on the location. This validation includes confirming the uniqueness of the location agency code and site number combination, as well as the uniqueness of the normalized station name. These validations are done here, rather than in the MLR Validator service, because they require making queries against the MLR Database to confirm and are strictly related to validating an incoming location against the current contents of the database. The MLR Validator service, conversely, validates whether an incoming monitoring location itself is valid, regardless of the current contents of the database. This includes validating parameter values and other related things but not the uniqueness of the location within the database.

## Local Configuration (non-Docker)

It is recommended to take advantage of the [mlr-local-dev](https://github.com/USGS-CIDA/mlr-local-dev) convenience package to bring up mlr-legacy-db unless you need to run it outside of local dev or outside of the MLR_Legacy_DB project.

You will need a postgreSQL database to run this application. A Dockerized version is available at <https://github.com/USGS-CIDA/MLR_Legacy_DB.>
The configuration is located in src/main/results/application.yml. You will need to create an application.yml file in your local project's root directory to provide the variable values. It should contain:

```yaml
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

## Automated Testing

This application has two flavors of automated tests: unit tests (in the gov.usgs.wma.mlrlegacy package) and integration tests (in the gov.usgs.wma.mlrlegacy.db package) requiring a database. The unit tests can be run in isolation according to your normal practices.
The integration tests can be run in a terminal with the maven command ```mvn verify -P it``` in the project's root directory. Running in this manner will pull the database Docker image from the central repository and run it in a container.
They can also be run in your IDE against a database accessible to you. (Note that you should not use a shared database as the tests will destroy data and may have contention issues with other processes accessing the database.)
In either case, configuration information will be pulled from the maven setting.xml file. It will need to contain the following profile:

```xml
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

### Setting up SSL

This application is configured to run over HTTPS and thus requires SSL certificates to be setup before it can be run via Docker. When running this container alone and not with an MLR Local Dev setup SSL certificates can be configured easily by simply running the included `create_keys.sh` script in the `docker/certificates` directory.

When intending to run this application alongside other MLR service running from the MLR Local Dev project you should use the certificate files generated by the MLR Local Dev project. This is important because in order for the MLR Local Dev services to connect to this service they must trust the certificate it is serving, which is most easily accomplished locally by using the same certificate for SSL among all of the MLR services.

In addition to its own SSL certs, this service must also be able to connect to a running Water Auth server locally, and thus must trust the SSL certificate being served by Water Auth. This can be accomplished by copy-pasting the .crt file that Water Auth is serving into the `docker/certificates/import_certs` folder of this project. Any .crt file put into the `import_certs` directory will be loaded into the certificate store used by Python within the container and trusted by the application.

When using MLR Local Dev this means copying the certificates that MLR Local Dev generates into 2 places in this project:

1. `docker/certificates` to be used as the SSL certs served by this service

2. `docker/certificates/import_certs` to have this service trust other services serving the MLR Local Dev SSL certs

### Building Changes

To build and run the application after completing the above steps you can run: `docker-compose up --build {profile}`, replacing `{profile}` with one of the options listed above.

The swagger documentation can then be accessed at <https://localhost:6010/swagger-ui.html>
