const calc = require("./imp/Calc");
const cookie = require("./imp/Cookie");
const costfuns = require("./imp/Costfuns");
const dateparse = require("./imp/DateParse");
const filesuffix = require("./imp/FileSuffix");
const notypevar = require("./imp/NotyPevar");
const ordered4 = require("./imp/Ordered4");
const pat = require("./imp/Pat");
const regex = require("./imp/Regex");
const text2txt = require("./imp/Text2txt");
const title = require("./imp/Title");

const bodyParser = require("body-parser");
const express = require("express");
const app = express();
app.use(bodyParser.json());

app.get("/api/ordered4/:w/:x/:z/:y", (req, res) => {

    const w = req.params.w;
    const x = req.params.x;
    const z = req.params.z;
    const y = req.params.y;

    res.json(ordered4(w, x, z, y));
});


app.get("/api/notypevar/:i/:s", (req, res) => {

    const i = parseInt(req.params.i);
    const s = req.params.s;

    res.json(notypevar(i, s));
});


app.get("/api/filesuffix/:directory/:file", (req, res) => {

    const directory = req.params.directory;
    const file = req.params.file;

    res.json(filesuffix(directory, file));
});


app.get("/api/dateparse/:dayname/:monthname", (req, res) => {

    const dayname = req.params.dayname;
    const monthname = req.params.monthname;

    res.json(dateparse(dayname, monthname));
});


app.get("/api/costfuns/:i/:s", (req, res) => {

    const i = parseInt(req.params.i);
    const s = req.params.s;

    res.json(costfuns(i, s));
});


app.get("/api/cookie/:name/:val/:site", (req, res) => {

    const name = req.params.name;
    const val = req.params.val;
    const site = req.params.site;

    res.json(cookie(name, val, site));
});


app.get("/api/calc/:op/:arg1/:arg2", (req, res) => {

    const op = req.params.op;
    const arg1 = parseDouble(req.params.arg1);
    const arg2 = parseDouble(req.params.arg2);

    res.json(calc(op, arg1, arg2));
});


app.get("/api/text2txt/:word1/:word2/:word3", (req, res) => {

    const word1 = req.params.word1;
    const word2 = req.params.word2;
    const word3 = req.params.word3;

    res.json(text2txt(word1, word2, word3));
});


app.get("/api/title/:sex/:title", (req, res) => {

    const sex = req.params.sex;
    const titleInput = req.params.title;

    res.json(title(sex, titleInput));
});


app.get("/api/pat/:txt", (req, res) => {

    const txt = rep.params.txt;

    res.json(regex(txt));
});


app.get("/api/pat/:txt/:pat", (req, res) => {

    const txt = req.params.txt;
    const patInput = req.params.pat;

    res.json(pat(txt, patInput));
});


app.get("/swagger.json", (req, res) => {

    const swagger = {
        "swagger": "2.0",
        "info": {
            "description": "Examples of different string algorithms accessible via REST",
            "version": "1.0",
            "title": "API for String Case Study (SCS)"
        },
        "host": "localhost:8080",
        "basePath": "/",
        "tags": [{"name": "scs-rest", "description": "Scs Rest"}],
        "paths": {
            "/api/calc/{op}/{arg1}/{arg2}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "calc",
                    "operationId": "calcUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "arg1",
                        "in": "path",
                        "description": "arg1",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }, {
                        "name": "arg2",
                        "in": "path",
                        "description": "arg2",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }, {"name": "op", "in": "path", "description": "op", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/cookie/{name}/{val}/{site}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "cookie",
                    "operationId": "cookieUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "name",
                        "in": "path",
                        "description": "name",
                        "required": true,
                        "type": "string"
                    }, {
                        "name": "site",
                        "in": "path",
                        "description": "site",
                        "required": true,
                        "type": "string"
                    }, {"name": "val", "in": "path", "description": "val", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/costfuns/{i}/{s}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "costfuns",
                    "operationId": "costfunsUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "i",
                        "in": "path",
                        "description": "i",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {"name": "s", "in": "path", "description": "s", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/dateparse/{dayname}/{monthname}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "dateParse",
                    "operationId": "dateParseUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "dayname",
                        "in": "path",
                        "description": "dayname",
                        "required": true,
                        "type": "string"
                    }, {
                        "name": "monthname",
                        "in": "path",
                        "description": "monthname",
                        "required": true,
                        "type": "string"
                    }],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/filesuffix/{directory}/{file}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "fileSuffix",
                    "operationId": "fileSuffixUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "directory",
                        "in": "path",
                        "description": "directory",
                        "required": true,
                        "type": "string"
                    }, {"name": "file", "in": "path", "description": "file", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/notypevar/{i}/{s}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "notyPevar",
                    "operationId": "notyPevarUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "i",
                        "in": "path",
                        "description": "i",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {"name": "s", "in": "path", "description": "s", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/ordered4/{w}/{x}/{z}/{y}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "ordered4",
                    "operationId": "ordered4UsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "w",
                        "in": "path",
                        "description": "w",
                        "required": true,
                        "type": "string"
                    }, {
                        "name": "x",
                        "in": "path",
                        "description": "x",
                        "required": true,
                        "type": "string"
                    }, {
                        "name": "y",
                        "in": "path",
                        "description": "y",
                        "required": true,
                        "type": "string"
                    }, {"name": "z", "in": "path", "description": "z", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/pat/{txt}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "regex",
                    "operationId": "regexUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "txt",
                        "in": "path",
                        "description": "txt",
                        "required": true,
                        "type": "string"
                    }],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/pat/{txt}/{pat}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "pat",
                    "operationId": "patUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "pat",
                        "in": "path",
                        "description": "pat",
                        "required": true,
                        "type": "string"
                    }, {"name": "txt", "in": "path", "description": "txt", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/text2txt/{word1}/{word2}/{word3}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "text2txt",
                    "operationId": "text2txtUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "word1",
                        "in": "path",
                        "description": "word1",
                        "required": true,
                        "type": "string"
                    }, {
                        "name": "word2",
                        "in": "path",
                        "description": "word2",
                        "required": true,
                        "type": "string"
                    }, {"name": "word3", "in": "path", "description": "word3", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/title/{sex}/{title}": {
                "get": {
                    "tags": ["scs-rest"],
                    "summary": "title",
                    "operationId": "titleUsingGET",
                    "produces": ["*/*"],
                    "parameters": [{
                        "name": "sex",
                        "in": "path",
                        "description": "sex",
                        "required": true,
                        "type": "string"
                    }, {"name": "title", "in": "path", "description": "title", "required": true, "type": "string"}],
                    "responses": {
                        "200": {"description": "OK", "schema": {"type": "string"}},
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            }
        }
    };


    res.status(200);
    res.json(swagger);
});


module.exports = app;