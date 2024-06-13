# Entity [![](https://img./github/release/Marthym/entity.svg)](https://GitHub.com/Marthym/entity/releases/) [![GitHub license](https://img.shields.io/github/license/Marthym/entity.svg)](https://github.com/Marthym/entity/blob/master/LICENSE)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Marthym_entity&metric=alert_status)](https://sonarcloud.io/dashboard?id=Marthym_entity)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Marthym_entity&metric=coverage)](https://sonarcloud.io/dashboard?id=Marthym_entity)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Marthym_entity&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=Marthym_entity)

<img src="favicon.png" alt="Size Limit CLI" align="right" style="float: right; margin: auto; width: 100px">

**Entity** is a small library that handles the notion of model entity. **Entity** offers an elegant way of working with
business objects that may or may not have an identifier.

The entity can hold, or not, some metadata.
For example ID of the user who creates the entity, the date of creation, the last update, ...
This metadata was embedded through an EnumMap, the most efficient structure to store unknown number of key/value pairs.

The jackson package offers a module for serializing and deserializing these entities in a flat manner.

## Installation

Use the package manager [maven](https://maven.apache.org/) to install entity.

```xml

<dependency>
    <groupId>fr.ght1pc9kc</groupId>
    <artifactId>entity-api</artifactId>
    <version>VERSION</version>
</dependency>
<dependency>
    <groupId>fr.ght1pc9kc</groupId>
    <artifactId>entity-jackson</artifactId>
    <version>VERSION</version>
</dependency>
<dependency>
    <groupId>fr.ght1pc9kc</groupId>
    <artifactId>entity-graphql</artifactId>
    <version>VERSION</version>
</dependency>
```

for gradle

```groovy
compile "fr.ght1pc9kc:entity-api:VERSION"
compile "fr.ght1pc9kc:entity-jackson:VERSION"
compile "fr.ght1pc9kc:entity-graphql:VERSION"
```

## Usage

### Create an entity

```java
import fr.ght1pc9kc.entity.api.Entity;
import java.time.Instant;

import static fr.ght1pc9kc.demo.DefaultMeta.createdBy;
import static fr.ght1pc9kc.demo.DefaultMeta.createdAt;

Entity<String> basic = Entity.identify("May the force")
        .withId("4TH");

Entity<String> simple = Entity.identify("May the force")
        .meta(createdBy, "Yoda")
        .meta(createdAt, Instant.now())
        .withId("4TH");

Entity<String> verySimple = Entity.identify("May the force").withId("4TH");

Entity<Saber> POLYMORPHIC_ENTITY = Entity
        .<Saber>identify(new LightSaber(Color.GREEN, 1))
        .meta(createdAt, Instant.parse("2024-01-20T15:06:42.546Z"))
        .meta(createdBy, "okenobi")
        .withId("LIGHTSABER");
```

### Include Jackson module

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.ght1pc9kc.entity.jackson.EntityModule;

ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .registerModule(new EntityModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

Using `JavaTimeModule` is mandatory to allow serialize `Instant`, `WRITE_DATES_AS_TIMESTAMPS` is optional but provide
more beautiful json.

```json
{
  "_id": "LIGHTSABER",
  "_createdAt": "2024-01-20T15:06:42.546Z",
  "_createdBy": "okenobi",
  "@type": "LIGHT",
  "blade": 1,
  "color": "GREEN"
}
```

### Use GrapQL DataFetcher module

In Spring Boot, create a configuration classe into your module 

```java
@Slf4j
@Configuration
public class MyObjectEntityConfiguration {
    @Bean
    public RuntimeWiringConfigurer customRuntimeWiringConfigurer(ObjectMapper mapper) {
        // Create the DataFetcher by passing the configured Jackson Object Mapper
        DataFetcher<Object> dataFetcher = new EntityDataFetcher(mapper);
        // Create a TypeResolver which detect Entity object
        TypeResolver typeResolver = new EntityTypeResolver();

        // customize WiringConfigurer to map possible Entity self object to use EntityDataFetcher
        return wiringBuilder -> wiringBuilder
                .type(MyObject.class.getSimpleName(), builder -> builder.defaultDataFetcher(dataFetcher).typeResolver(typeResolver));
    }
}
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)
