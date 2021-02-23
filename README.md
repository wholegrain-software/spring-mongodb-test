# Spring MongoDB Test

Spring MongoDB Test is a TestExecutionListener that simplifies seeding the database in spring-data-mongodb integration
tests.

## Dependency

### Gradle

```groovy
testImplementation 'com.wholegrainsoftware:spring-mongodb-test:{latest}'
```

### Maven

```xml

<dependency>
    <groupId>com.wholegrainsoftware</groupId>
    <artifactId>spring-mongodb-test</artifactId>
    <version>{latest}</version>
    <scope>test</scope>
</dependency>
```

## Setup

The following lines of code add the `MongoDBTestExecutionListener` to the SpringBootTest lifecycle.

```java
import com.wholegrainsoftware.springmongotest.MongoDBTestExecutionListener;

@SpringBootTest
@TestExecutionListeners(
        value = {MongoDBTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class MyIntegrationTest {
    // ...
}
```

The database will be determined through one of the following properties:

```yaml
spring.data.monogdb.database: my_mongo_database
# or
spring.data.mongodb.uri: mongodb://localhost:27017/my_mongo_database
```

In this case everything will be executed against `my_mongo_database`.

## Tests with Documents

The `@Doc` annotation allows inserting documents into the given database.

A typical test will look like this:

```java
import com.wholegrainsoftware.springmongotest.Doc;
import com.wholegrainsoftware.springmongotest.MongoDBTest;
import org.junit.jupiter.api.Test;

@Doc(collection = "person", files = {"/documents/john-doe.bson"})
public class MyTest extends MyIntegrationTest {
    @Test
    @MongoDBTest
    public void myTest() {
        // ...    
    }
}
```

A bson file, located in `<projectRoot>/src/test/resources/documents/john-doe.bson` could look like this:

```bson
{
    _id: ObjectId('6032473edd63e50bd3565171'),
    firstName: 'John',
    lastName: 'Doe',
    _class: 'person'
}
```

For the following entity:

```java

@Document("person")
@TypeAlias("person")
public class Person {
    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;

    //...
}
```

You can even aggregate multiple `@Doc` annotations into custom annotations like:

```java
import com.wholegrainsoftware.springmongotest.Doc;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Doc(collection = "person", files = {"/documents/john-doe.bson"})
@Doc(collection = "department", files = {"/documents/sales.bson"})
public @interface InsertSalesDepartment {
}
```

## Tests with Files

The `@GridFsFile` annotation allows inserting files into GridFs of the given database.

A test will look like this:

```java
import com.wholegrainsoftware.springmongotest.GridFsFile;
import com.wholegrainsoftware.springmongotest.MongoDBTest;
import org.junit.jupiter.api.Test;

@GridFsFile(id = "60327c7f9189342c201e0e11", filePath = "/files/test.pdf")
public class MyGridFsTest extends MyIntegrationTest {
    @Test
    @MongoDBTest
    public void myTest() {
        // ...    
    }
}
```

## Example

For a more thorough example, please check out the [example](example/spring-example). It contains a functional Spring Boot application
and several tests that showcase this library.

## License

Copyright (C) [Wholegrain Software](https://wholegrain-software.com) 2021

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.