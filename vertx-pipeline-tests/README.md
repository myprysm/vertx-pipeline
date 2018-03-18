# Vert.X Pipeline Tests

This is a small set of reusable components that helps to improve velocity in writting
unit tests, especially for Vert.X Pipeline.

They are intended to be non-obtrusive while bringing some "cool" features or helpers.

They are based on `Junit 5`

## Usage

Add it as a maven dependency to your project:
```
<dependency>
    <groupId>fr.myprysm</groupId>
    <artifactId>vertx-pipeline-tests</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## VertxTest

Interface that brings automatically the `Junit 5` `VertxExtension` as well as some class methods
to read resources from classpath (String, JsonObject, JsonArray, absolute path resolution).

## ConsoleTest

Abstract class that swallows standard output into a buffer to assert that console contains such value (or not).

Provides automatic `@BeforEach` and `@AfterEach` hooks to reset console to its original state
as well as helpers to validate content.