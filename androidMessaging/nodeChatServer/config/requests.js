var mongoose = require('mongoose');
var request = require('request');
var user = require('./dbModel.js');

exports.login = function(userName,mobileNum,reg_id,callback){
	var newUser = new user({
		name:userName,
		mobile_num:mobileNum,
		reg_id:reg_id
	});
	user.find({mobile_num:mobileNum},function(error,users){
		var length = users.length;
		// check if mobile number is already in database
		if(length == 0){
			newUser.save(function(err){
				callback({'response':'Successfully Registered'});
			});
		}else{
			callback({'response':'User Already Registered'});
		}
	});
}

exports.getUsers = function(mobileNumber,callback){
	user.find(function(err,users){
		var len = users.length;
		if(len == 0){
			callback({'response':"No Users Registered"});
		}else{
			callback(removeUserFromArr(users,mobileNumber));
		}
	});
}

function removeUserFromArr(arr, val) {
	for(var i=0; i<arr.length; i++) {
		if(arr[i].mobile_num == val) {
			arr.splice(i, 1);
			return arr;
		}
	}
	return arr;
}



exports.removeUser = function(mobileNumber, callback){
	console.log("removing mobile number: " + mobileNumber);
	user.remove({mobile_num: mobileNumber}, function(err){
		if(err){
			callback({'response': "Error removing user"});
		}else{
			callback({'response':"User removed successfully"});
		}
	});
}

exports.sendMessage = function(fromName, fromNumber, toNumber, message, callback){
	user.find({mobile_num: toNumber},function(error, users){
		if(users.length == 0){
			callback({'response': 'Failure'});
		}else{
			var to_id = users[0].reg_id;
			var to_name = users[0].name;
			// make an http request to google cloud messaging
			request(
			{
				method:'POST',
				uri:'https://android.googleapis.com/gcm/send',
				headers:{
					'Content-Type':'application/json',
					'Authorization':'key=AIzaSyAEWWWvfYkUNl2-RDAsOoxJafssgnu9oz0'
				},
				body: JSON.stringify({
					"registration_ids":[to_id],
					"data":{
						"message":message,
						"fromName":fromName,
						"fromNumber":fromNumber
					},
					"time_to_live":108
				})
			}, function(err,response,body){
				console.log(body);
				callback({'response':"Success"});
			});
		}
	});
}