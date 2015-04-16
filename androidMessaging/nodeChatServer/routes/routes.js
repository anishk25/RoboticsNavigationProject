var requestsModule = require("../config/requests.js");
var httpRequest = require('request');

module.exports = function(app){
	app.get('/', function(request,response){
		response.end("Node-Android chat application");
	});

	app.post('/login', function(request,response){
		var name = request.body.name;
		var mobile_number = request.body.mobile_num;
		var reg_id = request.body.reg_id;
		requestsModule.login(name,mobile_number,reg_id, function(found){
			console.log(found);
			response.json(found);
		});
	});

	app.post('/send', function(request,response){
		var fromName = request.body.fromName;
		var fromNumber = request.body.fromNumber;
		var toNumber = request.body.toNumber;
		var message = request.body.message;

		console.log("From Name: " + fromName);
		console.log("From Number: " + fromNumber);
		console.log("To Number: " + toNumber);
		requestsModule.sendMessage(fromName, fromNumber,toNumber,message, function(found){
			console.log(found);
			response.json(found);
		});
	});

	app.post('/getusers', function(request,response){
		var mobile_number = request.body.mobile_num;
		requestsModule.getUsers(mobile_number,function(found){
			console.log(found);
			response.json(found);
		});
	});

	app.post('/logout',function(request,response){
		var mobileNumber = request.body.mobile_num;
		requestsModule.removeUser(mobileNumber, function(found){
			console.log(found);
			response.json(found);
		});
	});
}