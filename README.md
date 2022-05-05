# How to generate new project (adidas recommended method)
In order to generate a new project based in the archetype you just need to type the command below in your terminal filling up the parameters with the data of your project.

`mvn org.apache.maven.plugins:maven-archetype-plugin:2.4:generate -DarchetypeCatalog=https://tools.adidas-group.com/artifactory/pea/com/adidas/springboot-archetype/archetype-catalog.xml -DarchetypeGroupId=com.adidas -DarchetypeArtifactId=springboot-archetype -DarchetypeVersion=0.0.3-SNAPSHOT -DgroupId=com.adidas.yourapplication -DartifactId=sample-microservice`

You have to fill up the parameters detailed below :

- groupId : Group of your application .
- artifactId : Name of your artifact.

Package structure generated
- common : All utility and classes contained common logic for the rest of packages must be located in this package
- config : All configuration classes must be located in this package.
- dto : All dto classes must be located in this package .
- mapper : All dto mapper must be located in this package.
- rest : All rest controller classes must be located in this package .
- hateoas : All hateoas assemblers classes must be located in this package.
- vo : All vo assemblers classes must be located in this package.
- service : All service classes must be located in this package

# How to make changes database schema

The database schema is managed by liquibase - a popularity open source project.
The core of using Liquibase is the changeLog file (`/src/resources/db.changelog/db.changelog-master.xml`) – an XML file 
that keeps track of all changes that need to run to update the DB. Liquibase migrations will run automatically on startup.
 Liquibase storing changelog in db via databasechangelog and databasechangeloglock tables.

## Add new database schema changes

If you need to make database schema changes - you should add new file with changeSet in `/src/resources/db.changelog/changes/` 
and add a reference to this file in `/src/resources/db.changelog/db.changelog-master.xml` - migrations will run automatically on startup
You can run changes manual by this command: `mvn liquibase:update`.

## Rollback changes
On prod env recommended create a new change file and remove necessary db objects.
Also you can do a rollback manualy by this commands:
 
- Rolling back to a tag - `mvn liquibase:rollback -Dliquibase.rollbackTag=1.0` This executes rollback statements of all the changesets executed after tag “1.0”.
- Rolling Back by Count - `mvn liquibase:rollback -Dliquibase.rollbackCount=1` Here, we define how many changesets we need to be rolled back. If we define it to be one, the last changeset execute will be rolled back.
- Rolling Back to Date - `mvn liquibase:rollback "-Dliquibase.rollbackDate=Jun 03, 2017"` We can set a rollback target as a date, therefore, any changeset executed after that day will be rolled back. The date format has to be an ISO data format or should match the value of DateFormat.getDateInstance() of the executing platform.

For more information about rollback go to official documentation https://docs.liquibase.com/workflows/liquibase-community/using-rollback.html
    
# How to name tests

[There](https://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html) is a popular way of test naming convention.
The basic naming of a test comprises of three main parts:

`[UnitOfWork_StateUnderTest_ExpectedBehavior]`

A **unit of work** is a use case in the system that startes with a public method and ends up with one of three types of results: a return value/exception, a state change to the system which changes its behavior, or a call to a third party (when we use mocks).
Test name should express a specific requirement, include the expected input or state and the expected result for that input or state.

Examples:
- `Sum_simpleValues_Calculated()`
- `Sum_NegativeNumberAs1stParam_ExceptionThrown()`
- `Sum_NumberIgnoredIfBiggerThan1000`
- `Parse_OnEmptyString_ExceptionThrown()`