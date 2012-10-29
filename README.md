# Gerrit Integration for Sonar
 
This is a sonar plugin that allows reporting to gerrit if any alerts were fired during sonar analysis.

## Installation
Simply copy the plugin to extensions/plugins directory and configure it in the general setting section then restart sonar.

You need to setup the passwordless connection to gerrit using public/private key so that sonar can communicate with gerrit.
You need to setup a user (recommended name: "sonar") in gerrit and use the public key for that user.

## Configuration of Projects

The plugin needs two types of configuration:
The configuration to determine what change to mark in gerrit:
gerrit.project - name of the project in gerrit
gerrit.change - the change number
gerrit.patch - the patch number

These parameters have to be passed to sonar with the analysis

### If you use maven just run

mvn sonar:sonar -Dgerrit.project=<projectname> -Dgerrit.change=<change> -Dgerrit.patch=<patch>

### If you use jenkins configure the sonar plugin so that these variables are set.

Just install the gerrit trigger plugin and use the $GERRIT_PROJECT $GERRIT_CHANGE_NUMBER$GERRIT_PATCHSET_NUMBER variables to specify maven parameters


