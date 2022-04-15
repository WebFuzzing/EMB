import { NestFactory } from '@nestjs/core';
import { ApplicationModule } from './app.module';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';

async function bootstrap() {
  const appOptions = {cors: true};
  const app = await NestFactory.create(ApplicationModule, appOptions);
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

  /*
  Added to be able to collect coverage with C8. See:
  https://github.com/bcoe/c8/issues/166
*/
//setTimeout(()=> process.exit(0), 10000)
// process.on("SIGINT", () =>{console.log("SIGINT"); process.exit(0)})
// process.on("SIGTERM", () =>{console.log("SIGTERM"); process.exit(0)})
// process.on("SIGUSR1", () =>{console.log("SIGUSR1"); process.exit(0)})
  app.getHttpAdapter().post("/shutdown", () => process.exit(0))


  app.use('/swagger.json', (req, res) => {
    res.status(200);
    res.json(require('../swagger.json'));
  });

  const port = process.env.PORT || 3000;

  //mysql is employed, docker run --name mysql_db -e MYSQL_ROOT_PASSWORD=test -e MYSQL_USER=test -e MYSQL_PASSWORD=test  -e MYSQL_DATABASE=test -p 3306:3306 -d mysql:5.7.22
  await app.listen(port);
}
bootstrap();