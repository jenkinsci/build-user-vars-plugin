## Changelog

### 1.5 (Dec 11, 2015)

-   Pipeline support
-   Requires 1.609 or higher of Jenkins.

### 1.4 (Oct 10, 2014)

-   Support of BUILD\_USER\_EMAIL. Currently, only the explicit e-mail
    definition in user properties will work (see [Mailer
    Plugin](https://wiki.jenkins.io/display/JENKINS/Mailer))
-   NPE on non-existent upstream builds
    ([JENKINS-22974](https://issues.jenkins-ci.org/browse/JENKINS-22974))

### 1.3 (Apr 12, 2014)

-   User variables are not set to Maven3 build
    ([JENKINS-19187](https://issues.jenkins-ci.org/browse/JENKINS-19187))

### 1.2

- [JENKINS-21955](https://issues.jenkins-ci.org/browse/JENKINS-21955) Set BUILD\_USER to "SCMTrigger" if a build is run
because of a source code change

### 1.1

- Now works with Jenkins starting from 1.396 version.

### 1.0

- Initial version
