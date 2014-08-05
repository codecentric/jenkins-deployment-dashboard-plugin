# Jenkins Deployment Dashboard

[![Build Status](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin.svg?branch=master)](https://travis-ci.org/codecentric/jenkins-deployment-dashboard-plugin)

## What's this about?

The jenkins-deployment-dashboard-plugin is about deploying your stuff easily. You configure an artifact repository (like artifactory or nexus) and one or more deployment servers. The configured plugin then lets you deploy any version of your software to the deployment environment you like (i.e. DEV, TEST, PROD).

At first we'll support amazon ec2 instances but our goal is, to make this plugin flexible enough to support all kinds of server instances.

## Prerequisites

Please add your AWS credentials to your user home to successfully run the EC2 unit tests 

```bash
# File: $USER_HOME/.aws/credentials 

[default]
aws_access_key_id = <KEY_ID>
aws_secret_access_key = <ACCESS_KEY>
```

## What do you need to make this work?

The plugin was constructed with the maven goal hpi:create, which generats a basic jenkins plugin skeleton. If you check out this repository you don't need to do this anymore. All you have to do is the following:

1. Check out the repo (like, duh...)
2. Run _mvn hpi:run_. This will start a jenkins with the plugin already installed and ready to use.
3. Locate http://localhost:8080/jenkins and enjoy...

## Developers

* Miel Donkers
* Andreas Houben
* Marcel Birkner
