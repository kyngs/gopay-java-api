/*
 * This file was generated by the Gradle 'init' task.

 */

rootProject.name = "gp-java-api-v3-parent"
include(":common-tests")
include(":gp-java-api-v3-apache-cxf")
include(":gp-java-api-v3-resteasy")
include(":gp-java-api-v3-apache-http-client")
include(":gp-java-api-v3-common")
project(":gp-java-api-v3-apache-cxf").projectDir = file("apache-cxf")
project(":gp-java-api-v3-resteasy").projectDir = file("resteasy")
project(":gp-java-api-v3-apache-http-client").projectDir = file("apache-http-client")
project(":gp-java-api-v3-common").projectDir = file("common")