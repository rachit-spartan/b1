# ELECTRONIC STORE

### How To Run

#### Database
To start the database (PostgresQL),
```./script/start-database.sh```

The script will start a PostgreSQL docker container on port 5432. Please use any PostgreSQL client to create a database called `electronic-store`

#### Redis
To start the Redis,
```./script/start-redis.sh```

#### Spring Boot application
```./gradlew bootRun```

PS: admin username and password are `user`
###Assumptions
1. When you add a product and specify the quantity, the specified quantity in the api will be final quantity and will override
previous quantity.


### TODOs
1. Flyway Migrations
2. Refactor the `adjustCart` i.e. maybe a different way to manage discount deals and bundle deals 
3. Definitely more tests for controllers, entity classes etc.
4. 