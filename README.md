# Entity [![](https://img./github/release/Marthym/entity.svg)](https://GitHub.com/Marthym/entity/releases/) [![GitHub license](https://img.shields.io/github/license/Marthym/entity.svg)](https://github.com/Marthym/entity/blob/master/LICENSE)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Marthym_entity&metric=alert_status)](https://sonarcloud.io/dashboard?id=Marthym_entity)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Marthym_entity&metric=coverage)](https://sonarcloud.io/dashboard?id=Marthym_entity)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Marthym_entity&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=Marthym_entity)

**Entity** is a small library that handles the notion of model entity. **Entity** offers an elegant way of working with
business objects that may or may not have an identifier.

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
```

for gradle

```groovy
compile "fr.ght1pc9kc:entity-api:VERSION"
compile "fr.ght1pc9kc:entity-jackson:VERSION"
```

## Usage

### Create an entity

```java
import fr.ght1pc9kc.entity.api.Entity;

Entity<String> simple = Entity.identify("May the force")
        .createdBy("Yoda")
        .createdAt(createdAt)
        .withId("4TH");

Entity<String> verySimple = Entity.identify("May the force").withId("4TH");

Entity<Saber> POLYMORPHIC_ENTITY = Entity
        .<Saber>identify(new LightSaber(Color.GREEN, 1))
        .createdAt(Instant.parse("2024-01-20T15:06:42.546Z"))
        .createdBy("okenobi")
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

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)
