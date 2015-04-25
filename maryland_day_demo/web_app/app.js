var express = require('express');
// define app using express
var app = express();
var port = process.env.PORT || 8080; // set up the port
var server = app.listen(port);
var io = require('socket.io').listen(server);


app.get('/',function(req,res){
	res.sendFile(__dirname + '/robot_control_web_app.html');
});


io.sockets.on('connection', function (socket) {
    socket.on('robot_command', function(msg){
    	console.log('message: ' + msg);
  	});
});

console.log("Server running on port " + port);