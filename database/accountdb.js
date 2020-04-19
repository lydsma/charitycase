var mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect('mongodb+srv://dbUser350:mn%25Q3e5%24@cluster0-q1ukr.gcp.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true });
var db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.info('Connected to db');
});

/*************************************************/

var Schema = mongoose.Schema;

var accountSchema = new Schema({    //for login
	name: String, 
    email: String,
    password: String
});

var profileSchema = new Schema({    //for signup
    email: String,
    name: String,
    status: String, 
    location: String
});

accountSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}
profileSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}

var Account = mongoose.model('Accounts', accountSchema);
var Profile = mongoose.model('Profiles', profileSchema);

/***********************DB calls**************************/

var createAccount_DB = function(nameInput, emailInput, passwordInput, callback) {
    var newAcct = new Account ({
		name: nameInput,
        email: emailInput,
        password: passwordInput
	});

	// save the person to the database
	newAcct.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
            console.log(err);
            callback(null, err);
		}
		else {
		    // display the "successfull created" page using EJS
            console.log('db> Success!' + newAcct);
            callback(newAcct, null);
		}
	    } ); 
};

var accountdb = {
    createAccount: createAccount_DB
}

module.exports = accountdb;