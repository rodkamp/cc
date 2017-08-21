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

In another terminal window run the Publisher:

    java -cp target/cc-solution-0.1-SNAPSHOT.jar com.gang.cc.Publisher

## TODO:
due to time limitation, the followings have not been fully implemented:
1. check whether manager a manager has ideal worker;  (learn whether ExecutorService can check ideal thread OR manage a thread safe integer to represent whether there are ideal workers;
2. stop the Publisher and Managers gracefully;
3. exception handling and logging;
4. javadoc;
5. unit test;
