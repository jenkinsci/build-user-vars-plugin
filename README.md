# Build User Vars Plugin

This plugin provides a set of environment variables that describe the
user who started the build.

## Variables provided

This plugin defines the following environment variables to describe the
user who started the build:

| Variable                 | Description                        |
|--------------------------|------------------------------------|
| BUILD\_USER              | Full name (first name + last name) |
| BUILD\_USER\_FIRST\_NAME | First name                         |
| BUILD\_USER\_LAST\_NAME  | Last name                          |
| BUILD\_USER\_ID          | Jenkins user ID                    |
| BUILD\_USER\_GROUPS      | Jenkins user groups                |
| BUILD\_USER\_EMAIL       | Email address                      |

## Usage example

Select *Set Jenkins user build variables* and reference the variables
during the build:

![](docs/images/build-user-vars-plugin-sample-usage.png)

## Pipeline Examples

**Script**  Expand source

```groovy
node {
  wrap([$class: 'BuildUser']) {
    def user = env.BUILD_USER_ID
  }
}
```

## Jenkins core compatibility

This plugin requires Jenkins 1.609+ (since 1.5 version of plugin).
