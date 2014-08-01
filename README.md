#jenkins-deployment-dashboard-plugin

Jenkins plugin for a deployment dashboard

## What's this about?

The jenkins-deployment-dashboard-plugin is about deploying your stuff easily. You configure an artifact repository (like artifactory or nexus) and one or more deployment servers. The configured plugin then lets you deploy any version of your software to the deployment environment you like (i.e. DEV, TEST, PROD).

At first we'll support amazon ec2 instances but our goal is, to make this plugin flexible enough to support all kinds of server instances.

## What do you need to make this work?

The plugin was constructed with the maven goal hpi:create, which generats a basic jenkins plugin skeleton. If you check out this repository you don't need to do this anymore. All you have to do is the following:

1. Check out the repo (like, duh...)
2. Edit your maven settings.xml. It needs to contain the following lines:
 ```xml
<settings>
  <pluginGroups>
    <pluginGroup>org.jenkins-ci.tools</pluginGroup>
  </pluginGroups>
  <profiles>
    <profile>
      <id>jenkins</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>repo.jenkins-ci.org</id>
          <url>http://repo.jenkins-ci.org/public/</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>repo.jenkins-ci.org</id>
          <url>http://repo.jenkins-ci.org/public/</url>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
</settings>
```

 This of course implies that you have a maven set up and running.

3. Run _mvn hpi:run_. This will start a jenkins with the plugin already installed and ready to use.
4. Locate http://localhost:8080/jenkins and enjoy...


## Developers

* Miel Donkers
* Andreas Houben
* Marcel Birkner
