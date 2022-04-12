module.exports = {
    "type": "mysql",
    "host": "localhost",
    "port": 3306,
    "username": "test",
    "password": "test",
    "database": "test",
    "entities": [ process.env.NODE_ENV == 'production' ? __dirname + '/build/src/**/**.entity.js' : (process.env.NODE_ENV == 'em_production'? __dirname + '/instrumented/src/**/**.entity.js':__dirname + '/build/src/**/**.entity.js') ],
    "migrationsTableName": "migrations",
    "migrations": [process.env.NODE_ENV == 'em_production'? "instrumented/src/database/migration/*.js": "build/src/database/migration/*.js"],
    "cli": {
        "migrationsDir": process.env.NODE_ENV == 'em_production'? "instrumented/src/database/migration": "build/src/database/migration"
    },
    "synchronize": true
}