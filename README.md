Spring Boot template for REST API server
==========

This repository contains Spring Boot (https://spring.io/projects/spring-boot) 
application template for REST API server.

Requirements
----------
* Java 1.8 latest update installed
* Access to maven central and jcenter repositories
* Grafana (https://grafana.com/) for metrics (tested on version 6.1.6)
* InfluxDB (https://www.influxdata.com/) for metrics (tested on version 1.7.6)

Features
----------
* Creates zip package ready for distribution.
* Provides setup for easy configuration by providing configuration files (application.properties and log4j2.xml)
outside jar package.
* Production ready log4j2 configuration: daily rolling file; zipping archive log files; 
keeps files only for last 2 weeks.
* Basic statistics gathering setup with micrometer and InfluxDB.
* Production ready Grafana dashboard.
* Basic setup for building REST API documentation with https://spring.io/projects/spring-restdocs.
* API documentation provided with application. 
* Basic authorization setup with spring security and HTTP basic authentication.
* Basic setup for CORS requests.
* Basic setup for application testing
* Custom error attributes implementation so that each API error has the same structure

**NOTE** search for TODO-s for places that should be adjusted to your needs

Building application
----------
Target creates packaged zip with executable jar (see build.gradle for exact location of zip package). 
API documentation is included with distribution also contains version information. 
To change version edit gradle.properties. application.properties and log4j config are provided outside 
application jar for easy configuration. Application may be also configured through environment variables 
eg. this may be used with docker images. 

```bash
gradlew build
```

or if you like to skip tests.

```bash
gradlew build -x test
```

**NOTE** that skipping tests will not generate API documentation properly. 
Documentation with missing snippets will be included in application distribution. 