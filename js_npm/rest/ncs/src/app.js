const {bessj} = require("./imp/BessJ");
const {expint} = require("./imp/Expint");
const {fisher} = require("./imp/Fisher");
const {gammq} = require("./imp/Gammq");
const {remainder} = require("./imp/Remainder");
const {triangle} = require("./imp/TriangleClassification");

const bodyParser = require("body-parser");
const express = require("express");
const app = express();
app.use(bodyParser.json());


app.get("/api/triangle/:a/:b/:c", (req, res) => {

    const a = parseInt(req.params.a);
    const b = parseInt(req.params.b);
    const c = parseInt(req.params.c);

    const dto = {resultAsInt: triangle(a, b, c)};

    res.json(dto);
});


app.get("/api/bessj/:n/:x", (req, res) => {

    const x = parseInt(req.params.x);
    const n = parseInt(req.params.n);

    if (n <= 2 || n > 100) {
        res.status(400);
        res.send();
        return;
    }

    const dto = {resultAsDouble: bessj(n, x)};

    res.json(dto);
});

app.get("/api/expint/:n/:x", (req, res) => {

    const x = parseInt(req.params.x);
    const n = parseInt(req.params.n);

    try {
        const dto = {resultAsDouble: expint(n, x)};
        res.json(dto);
    } catch (e) {
        res.status(400);
        res.send();
    }
});

app.get("/api/fisher/:m/:n/:x", (req, res) => {

    const x = parseInt(req.params.x);
    const n = parseInt(req.params.n);
    const m = parseInt(req.params.m);

    if (m > 1000 || n > 1000) {
        res.status(400);
        res.send();
        return;
    }

    try {
        const dto = {resultAsDouble: fisher(m, n, x)};
        res.json(dto);
    } catch (e) {
        res.status(400);
        res.send();
    }
});

app.get("/api/gammq/:a/:x", (req, res) => {

    const a = parseInt(req.params.a);
    const x = parseInt(req.params.x);

    try {
        const dto = {resultAsDouble: gammq(a, x)};
        res.json(dto);
    } catch (e) {
        res.status(400);
        res.send();
    }
});

app.get("/api/remainder/:a/:b", (req, res) => {

    const a = parseInt(req.params.a);
    const b = parseInt(req.params.b);

    const lim = 1000;
    if (a > lim || a < -lim || b > lim || b < -lim) {
        res.status(400);
        res.send();
        return;
    }

    const dto = {resultAsInt: remainder(a, b)};

    res.json(dto);
});


app.get("/swagger.json", (req, res) => {

    const swagger = {
        "swagger": "2.0",
        "info": {
            "description": "Examples of different numerical algorithms accessible via REST",
            "version": "1.0",
            "title": "API for Numerical Case Study (NCS)"
        },
        "host": "localhost:8080",
        "basePath": "/",
        "tags": [{"name": "ncs-rest", "description": "Ncs Rest"}],
        "paths": {
            "/api/bessj/{n}/{x}": {
                "get": {
                    "tags": ["ncs-rest"],
                    "summary": "bessj",
                    "operationId": "bessjUsingGET",
                    "produces": ["application/json"],
                    "parameters": [{
                        "name": "n",
                        "in": "path",
                        "description": "n",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "x",
                        "in": "path",
                        "description": "x",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }],
                    "responses": {
                        "200": {
                            "description": "OK",
                            "schema": {"$ref": "#/definitions/Dto", "originalRef": "Dto"}
                        },
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/expint/{n}/{x}": {
                "get": {
                    "tags": ["ncs-rest"],
                    "summary": "expint",
                    "operationId": "expintUsingGET",
                    "produces": ["application/json"],
                    "parameters": [{
                        "name": "n",
                        "in": "path",
                        "description": "n",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "x",
                        "in": "path",
                        "description": "x",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }],
                    "responses": {
                        "200": {
                            "description": "OK",
                            "schema": {"$ref": "#/definitions/Dto", "originalRef": "Dto"}
                        },
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/fisher/{m}/{n}/{x}": {
                "get": {
                    "tags": ["ncs-rest"],
                    "summary": "fisher",
                    "operationId": "fisherUsingGET",
                    "produces": ["application/json"],
                    "parameters": [{
                        "name": "m",
                        "in": "path",
                        "description": "m",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "n",
                        "in": "path",
                        "description": "n",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "x",
                        "in": "path",
                        "description": "x",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }],
                    "responses": {
                        "200": {
                            "description": "OK",
                            "schema": {"$ref": "#/definitions/Dto", "originalRef": "Dto"}
                        },
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/gammq/{a}/{x}": {
                "get": {
                    "tags": ["ncs-rest"],
                    "summary": "gammq",
                    "operationId": "gammqUsingGET",
                    "produces": ["application/json"],
                    "parameters": [{
                        "name": "a",
                        "in": "path",
                        "description": "a",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }, {
                        "name": "x",
                        "in": "path",
                        "description": "x",
                        "required": true,
                        "type": "number",
                        "format": "double"
                    }],
                    "responses": {
                        "200": {
                            "description": "OK",
                            "schema": {"$ref": "#/definitions/Dto", "originalRef": "Dto"}
                        },
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/remainder/{a}/{b}": {
                "get": {
                    "tags": ["ncs-rest"],
                    "summary": "remainder",
                    "operationId": "remainderUsingGET",
                    "produces": ["application/json"],
                    "parameters": [{
                        "name": "a",
                        "in": "path",
                        "description": "a",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "b",
                        "in": "path",
                        "description": "b",
                        "required": true,
                        "type": "integer",
                        "format": "int32"
                    }],
                    "responses": {
                        "200": {
                            "description": "OK",
                            "schema": {"$ref": "#/definitions/Dto", "originalRef": "Dto"}
                        },
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            },
            "/api/triangle/{a}/{b}/{c}": {
                "get": {
                    "tags": ["ncs-rest"],
                    "summary": "Check the triangle type of the given three edges",
                    "operationId": "checkTriangleUsingGET",
                    "produces": ["application/json"],
                    "parameters": [{
                        "name": "a",
                        "in": "path",
                        "description": "First edge",
                        "required": false,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "b",
                        "in": "path",
                        "description": "Second edge",
                        "required": false,
                        "type": "integer",
                        "format": "int32"
                    }, {
                        "name": "c",
                        "in": "path",
                        "description": "Third edge",
                        "required": false,
                        "type": "integer",
                        "format": "int32"
                    }],
                    "responses": {
                        "200": {
                            "description": "OK",
                            "schema": {"$ref": "#/definitions/Dto", "originalRef": "Dto"}
                        },
                        "401": {"description": "Unauthorized"},
                        "403": {"description": "Forbidden"},
                        "404": {"description": "Not Found"}
                    },
                    "deprecated": false
                }
            }
        },
        "definitions": {
            "Dto": {
                "type": "object",
                "properties": {
                    "resultAsDouble": {"type": "number", "format": "double"},
                    "resultAsInt": {"type": "integer", "format": "int32"}
                },
                "title": "Dto"
            }
        }
    };

    res.status(200);
    res.json(swagger);
});


module.exports = app;
