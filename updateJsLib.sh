#!/bin/bash
./gradlew :actor-sdk:sdk-core:core:core-js:buildPackage
cp actor-sdk/sdk-core/core/core-js/build/package/actor.nocache.js ActorSDK-Web/node_modules/actor-js/actor.js
