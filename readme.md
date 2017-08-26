## Overview

This is the solution for coding challenge.

## Prereqs

- Install ActiveMQ
- Install [Maven](http://maven.apache.org/download.html) 

## Building

Run:

    mvn install

## Running the Examples

In one terminal window:
    start ActiveMQ (per ActiveMQ's guide)
    
In three terminal windows run the 3 Managers

    java -cp target/cc-solution-0.1-SNAPSHOT.jar com.gang.cc.Manager
    
    Note: Worker is implemented as an inner class of Manager

In another terminal window run the Publisher:

    java -cp target/cc-solution-0.1-SNAPSHOT.jar com.gang.cc.Publisher

## TODO:
due to time limitation, also to be focus on the main logic, the followings have not been implemented:
1. stop the Publisher and Managers gracefully;
2. exception handling and logging;
3. configuration of parameters, e.g. number of works, number of jobs, instead of hard-coded ones;
4. javadoc;
5. unit test;
