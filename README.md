# Jenkins EC2 Deployment Dashboard

[![Build Status](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin.svg?branch=master)](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin)

## Description

This plugin was developed to help managing the deployment of software artifacts to different environments easily. You configure an artifact repository (like Artifactory or Nexus) and your Amazon EC2 deployment servers. The plugin manages the deployed versions of any artifact from your repository to your server environments (i.e. DEV, TEST, PROD). The plugin works with Amazon EC2 instances. 

![EC2 Deployment Dashboard](documentation/1-dashboard-view.png)

## Plugin Documentation

For more details on how to use the plugin see our documentation.

* [Getting started & screenshots](documentation/README.md)
* [Blog article describing the plugin](https://blog.codecentric.de/en/2015/02/jenkins-deployment-dashboard-ec2-environments/)

Feel free to use the plugin and get in contact with us in case you have questions or suggestions.

## Building the plugin yourself

The plugin was constructed with the maven goal **hpi:create**, which generates a basic Jenkins plugin skeleton. 
To build the plugin yourself follow these steps:

* Check out the repository
* Run **mvn hpi:run**. This will start Jenkins with the plugin already installed and ready to use.
* Open jenkins [http://localhost:8080/jenkins](http://localhost:8080/jenkins)

## Testing the plugin

For testing the integration of the plugin with Artifactory or Nexus simply use Docker to startup a container.

* Artifactory **docker run -p 8082:8080 --name artifactory mattgruter/artifactory**
 * Open Artifactory: [http://localhost:8082](http://localhost:8082)
* Nexus **docker run -d -p 8081:8081 --name nexus sonatype/nexus**
 * Open Nexus: [http://localhost:8081](http://localhost:8081)

## Developers

* Miel Donkers
* Marcel Birkner
* Andreas Houben
* Christian Langmann 
