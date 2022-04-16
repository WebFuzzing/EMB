module.exports = {
    "type": "mysql",
    "host": "localhost",
    "port": process.env.DB_PORT || 3306,
    "username": "test",
    "password": "test",
    "database": "test",
    "entities": [ process.env.NODE_ENV == 'production' ? __dirname + '/build/src/**/**.entity.js' :  __dirname + '/instrumented/src/**/**.entity.js' ],
    "migrationsTableName": "migrations",
    "migrations": [process.env.NODE_ENV == 'production'? "build/src/database/migration/*.js": "instrumented/src/database/migration/*.js"],
    "cli": {
        "migrationsDir": process.env.NODE_ENV == 'production'? "build/src/database/migration": "instrumented/src/database/migration"
    },
    "synchronize": true
}