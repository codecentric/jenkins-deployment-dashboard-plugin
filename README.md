# Jenkins Deployment Dashboard

[![Build Status](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin.svg?branch=master)](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin)

## Description

This plugin is about managing and deploying your software artifacts easily. You configure an artifact repository (like Artifactory or Nexus) and your 
Amazon EC2 deployment servers. The plugin lets you deploy any artifact from your repository to your deployment environments (i.e. DEV, TEST, PROD).

At first we'll only support Amazon EC2 instances but our goal is, to make this plugin flexible enough to support different types of server instances.

## Building the plugin

The plugin was constructed with the maven goal *hpi:create*, which generates a basic Jenkins plugin skeleton. 
To build the plugin yourself follow these steps:

* Check out the repository
* Run **mvn hpi:run**. This will start Jenkins with the plugin already installed and ready to use.
* Open jenkins http://localhost:8080/jenkins

## Plugin Documentation

For more details on how to use the plugin see our documentation.

* [Getting started & screenshots](documentation/README.md)

Feel free to use the plugin and get in contact with us in case you have questions or suggestions.

## Developers

* Miel Donkers
* Andreas Houben
* Marcel Birkner
