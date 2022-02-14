const http  = require("http");
const app = require("../src/app");
const Router = require('koa-router');
const openapi = require('./openapi.json')


const router = new Router();

router.get('/openapi.json', (ctx, next) => {
    ctx.status = 200;
    ctx.body = openapi;
});
app.use(router.routes());

const server = http.createServer(app.callback());

module.exports = server;