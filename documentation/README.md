# Documentation

## Prerequisites

* Your are using Amazon EC2 instances for your servers?
* Your are using Artifactory or Nexus for storing your software artifacts?

Then you are all set to use this plugin :-)

## How to use the plugin

### Dashboard View

This Jenkins dashboard gives you an overview of all your EC2 instances that you configured. It shows the current state, the version deployed, uptime, instance type and the current IP. 
It also gets all artifact versions from your artifact repository. Currently we support jFrog Artifactory and Sonatype Nexus.

![Dashboard View](1-dashboard.png)

### Dashboard View Configuration

* configure your AWS credentials
* configure your repository (Artifactory or Nexus)
 
![Dashboard View configuration](2-config.png)

### Deployment Job Configuration

* create a parameterized deployJob (Parameter: ENVIRONMENT & VERSION)
* configure your AWS credentials
* within this job, configure your Deployment (via ShellScript/Batch/Puppet/Chef/ ...)

![Deployment Job configuration](3-deployJob.png)

### AWS Details

AWS instances can be tagged with custom tags. This plugin uses tags to identify the specified environments and stores the deployed software version in that tag. Here is an example screenshot from the AWS Management Console.

![AWS configuration](4-aws-tags.png)

### AWS Credentials

Please follow these steps to configure the necessary AWS Credentials:

* Open https://console.aws.amazon.com/iam/home?#users
* Create new IAM User
* Attach User Policy -> Custom Policy -> Select -> Set Policy Name -> Paste Policy statement from below
 * allow User to create tags
 * allow User to delete tags
 * allow User to describe instances

Policy Statement: 

``` bash
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "Stmt1412928158000",
      "Effect": "Allow",
      "Action": [
        "ec2:DeleteTags",
        "ec2:DescribeInstances",
        "ec2:CreateTags"
      ],
      "Resource": [
        "*"
      ]
    }
  ]
}
```
