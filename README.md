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
To start the application, ```./gradlew bootRun```

#### Run tests
To run all the tests, ```./gradlew clean test```

###Assumptions/Working
1. When you add a product and specify the quantity, the specified quantity in the api will be final quantity and will override
previous quantity.
2. When you add a main product, a bundled product for that main product is added automatically. If main product is removed, the
bundled product stays in the cart with full price (not free anymore)

PS: admin username and password are `user` and `user` for using admin apis

### TODOs
1. Flyway Migrations
2. Refactor the `adjustCart` i.e. maybe a different way to manage discount deals and bundle deals 
3. Definitely more tests for controllers, entity classes etc.
4. Load data from `.json` files for SSTs