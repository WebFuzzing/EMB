# Wicket Forms

This module encapsulates functionality to quickly build complex forms using Apache Wicket, Bootstrap 3, on top of JPA 2.0 entities.

It provides various types of pre-built form components, all with available validation, i18n, lists with entities, CRUD interface functionality.

The components are developed using the great project [wicket-bootstrap](https://github.com/l0rdn1kk0n/wicket-bootstrap).

## Form components

* TextFieldBootstrapFormComponent - this enapsulates functionality for text fields
* TextAreasFieldBootstrapFormComponent - same as above, but for TextAreasFieldBootstrapFormComponent
* Select2ChoiceBootstrapFormComponent - this is a single select created using  [wicket-select2](https://github.com/ivaynberg/wicket-select2) over JPA entities.
* Select2MultiChoiceBootstrapFormComponent  this is a multi-select created using  [wicket-select2](https://github.com/ivaynberg/wicket-select2) over JPA entities.
*  PercentageFieldBootstrapFormComponent - this is a textfield that is modified for displaying/validating percentages.
*  PasswordFieldBootstrapFormComponent - a textfield with a mask
* FileInputBootstrapFormComponent - a file upload component for JPA entities
* DateFieldBootstrapFormComponent - a date field component
* CheckBoxBootstrapFormComponent - a checkbox component
* TwitterFieldBootstrapFormComponent - a component for the Twitter id

For a working example, check the TestForm.java JPA entity (available under the persistence module) and its wrapping Wicket management interface: ListTestFormPage.java and EditTestFormPage.java.

## A few words about packaging

This module is packaged as a fat jar. For testing purposes, the default configuration will also start the web, persistence, reporting and ui modules. These can be easily turned on or off be adding or removing <dependencies> in pom.xml :

```
       <dependency>
           <groupId>org.devgateway.toolkit</groupId>
           <artifactId>persistence</artifactId>
           <version>0.0.1-SNAPSHOT</version>
       </dependency>

       <dependency>
           <groupId>org.devgateway.toolkit</groupId>
           <artifactId>ui</artifactId>
           <version>0.0.1-SNAPSHOT</version>
       </dependency>

       <dependency>
           <groupId>org.devgateway.toolkit</groupId>
           <artifactId>reporting</artifactId>
           <version>0.0.1-SNAPSHOT</version>
      </dependency>

      <dependency>
          <groupId>org.devgateway.toolkit</groupId>
          <artifactId>web</artifactId>
          <version>0.0.1-SNAPSHOT</version>
      </dependency>
 ```


 If dependencies are included, the related module will be started along with all its services. Please note it is not required to have the Forms module as the entry point, you can exclude forms+reports completely and just use dg-toolit with for example web, persistence  and ui.

## Running

 You can run the forms module just as any module of dg-toolkit:


 `java -Dspring.profiles.active=dev -jar target/forms-0.0.1-SNAPSHOT.jar`

 This will start everything, including an embedded Tomcat Web server and all the services attached it.
