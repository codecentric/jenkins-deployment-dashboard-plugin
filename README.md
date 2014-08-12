# Jenkins Deployment Dashboard

[![Build Status](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin.svg?branch=master)](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin)

## Plugin Documentation

* [Getting started & screenshots](documentation/README.md)

## What is this plugin about?

This plugin is about managing and deploying your software releases easily. You configure an artifact repository (like Artifactory or Nexus) and your Amazon EC2 deployment servers. The configured plugin lets you deploy any version of your software to the deployment environment you like (i.e. DEV, TEST, PROD).

At first we'll support Amazon EC2 instances but our goal is, to make this plugin flexible enough to support all kinds of server instances.

## What do you need to make this work?

The plugin was constructed with the maven goal hpi:create, which generates a basic Jenkins plugin skeleton. To get up and running with this plugin all you have to do is the following:

* Check out the repo (like, duh...)
* Run _mvn hpi:run_. This will start Jenkins with the plugin already installed and ready to use.
* Locate http://localhost:8080/jenkins and enjoy...

## Developers

* Miel Donkers
* Andreas Houben
* Marcel Birkner
