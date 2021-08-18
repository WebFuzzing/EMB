import { NestFactory } from '@nestjs/core';
import { ApplicationModule } from './app.module';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import {getConnectionOptions} from "typeorm";

let app;

module.exports ={

  bootstrap: async (port) =>{
    const appOptions = {cors: true};

    // Man added for EM
    // read connection options from ormconfig file (or ENV variables)
    const connectionOptions = await getConnectionOptions();
    // modify the value of port
    Object.assign(connectionOptions, { port: process.env.DB_PORT || 3306});

    app = await NestFactory.create(ApplicationModule, appOptions);
    app.setGlobalPrefix('api');

    const options = new DocumentBuilder()
        .setTitle('NestJS Realworld Example App')
        .setDescription('The Realworld API description')
        .setVersion('1.0')
        .setBasePath('api')
        .addBearerAuth()
        .build();
    const document = SwaggerModule.createDocument(app, options);

    //Man: there exists some problems with auto-generated swagger, i.e., lack of parameter info
    SwaggerModule.setup('/docs', app, document);

    app.use('/swagger.json', (req, res) => {
      res.status(200);
      res.json(require('../swagger.json'));
    });


    return await app.listen(port);
  },

  stop: async () =>{
    await app?.close();
  }
}

