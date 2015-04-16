varSSvar mongoose = require('mongoose');
var Schema = mongoose.Schema;
var userSchema = mongoose.Schema({
	name:String,
	mobile_num:String,
	reg_id:String
});

mongoose.connect('mongodb://localhost:27017/AndroidChatDB');
module.exports = mongoose.model('users',userSchema);
