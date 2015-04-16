// load packages
var express = require('express');
var body_parser = require('body-parser');
var routes = require('./routes/routes.js');


// initialize app
var app = express();

var port = process.env.PORT||8080;

// Configuration
app.use(body_parser.json());
app.use(body_parser.urlencoded({extended:true}));

// app router
routes(app);
app.listen(port);
console.log('The App is running on port ' + port);
