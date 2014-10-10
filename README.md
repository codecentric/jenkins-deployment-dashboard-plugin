# Jenkins Deployment Dashboard

[![Build Status](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin.svg?branch=master)](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin)

## Plugin Documentation

* [Getting started & screenshots](documentation/README.md)

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
 * Configure your artifact repository and your AWS Credentials under http://localhost:8080/jenkins/manage
 * Create a new parameterized Job that we will use later for the deployment
  * Add a **Text Parameter** VERSION
  * Add a **Text Parameter** ENVIRONMENT
  * Add a **Build Step** and select **EC2 Environment** -> configure your AWS Credentials, see <a href="documentation/README.md">"How to create your AWS Credentials"</a>
 * Create a new View and select **Deployment Dashboard** under http://localhost:8080/jenkins/newView
 * Configure the View
  * Set the checkbox if you want to deploy artifacts using the dashboard view
  * Add **groupId** and **artifactId** of the artifact you want to deploy
  * Add your EC2 server environment you want to monitor and manage (in the dropdown list you should be able to find all available EC2 server)
  * For **Build Job** add the Job you configured previously
  * Save the configuration

Now you should be able to see the status of your EC2 instances, the versions tags and other details. On top of the dashboard you can select the available versions  
of the artifact and the environments you want to deploy to. When you click the **Deploy App** Button the deploy job will be triggered with two parameters:

* VERSION
* ENVIRONMENT

It is up to you now to take these parameters and pass them to your custom deployment scripts. On top the Deploy Job tags the environment with the version in 
order to update the deployment dashboard view.

Feel free to use the plugin and get in contact with us in case you have questions or suggestions.

## Developers

* Miel Donkers
* Andreas Houben
* Marcel Birkner
