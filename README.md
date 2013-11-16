# Gerrit Integration for Sonar
 
This is a sonar plugin that reports to gerrit if any alerts/warnings were fired during sonar analysis.

You need to setup the alerts/warnings in sonar's quality profile.

## Installation
Simply copy the plugin to extensions/plugins directory and configure it in the general setting section then restart sonar.

You need to setup the passwordless connection to gerrit using public/private key so that sonar can communicate with gerrit.

You need to setup a user (recommended name: "sonar") in gerrit and use the public key for that user.

## Configuration of Projects

The plugin needs some variables to determine what change to mark in gerrit:

**gerrit.project - name of the project in gerrit**

**gerrit.change - the change number**

**gerrit.patch - the patch number**

These parameters have to be passed to sonar with the analysis

### If you use maven just run

**mvn sonar:sonar -Dgerrit.project=projectname -Dgerrit.change=changenumber -Dgerrit.patch=patchnumber**

### If you use jenkins configure the sonar plugin so that these variables are set.

Just install the gerrit trigger plugin and use the 

**$GERRIT_PROJECT**

**$GERRIT_CHANGE_NUMBER**

**$GERRIT_PATCHSET_NUMBER**

variables to specify maven parameters

## Build environment

If you want to build from sources or modify the sonar gerrit-integration-plugin,
you should start by clone it from github.

To run the unit tests, a gerrit install must be available at host `localhost`,
port `29418`, and the account `sonar` created on it, and authorized with ssh.
The project assumes that the ssh private key for the user `sonar` is stored
at `~/.ssh/id_rsa_sonar`.

    $ ssh_keygen -C "Sonar bot" -t rsa -f  ~/.ssh/id_rsa_sonar
    $ cat ~/.ssh/id_rsa_sonar.pub | ssh -p 29418 sgala@localhost gerrit create-account sonar \
             --email sgala@apache.org --full-name "Sonar\ bot" --group "Non-Interactive\ Users" --ssh-key -

If all is well,

    $ mvn verify

will now build and test the project with no test failing.

This very project can be installed in the local gerrit for testings by doing:

    $ cd ${gerrit_install}/git
    $ git clone --bare git@github.com:<user>/gerrit-intergation-plugin.git gerrit-integration-plugin.git
    $ bin/gerrit.sh restart

<user> should be either @spafcio@ or, better, your own github user after you have forked the project there.
