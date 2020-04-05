var mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect('mongodb+srv://dbUser350:mn%25Q3e5%24@cluster0-q1ukr.gcp.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true });
var db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.info('Connected to account db');
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

    Account.findOne({email: emailInput}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
           console.log('Creating account');
           newAcct.save( (err) => { 
                if (err) {
                    res.type('html').status(200);
                    res.write('uh oh: ' + err);
                    console.log(err);
                    callback(null, err);
                } else {
                    // display the "successfull created" page using EJS
                    console.log('Successfully saved: ' + newAcct);
                    callback(newAcct, null);
                }
            }); 
        } else {
            console.log('account exists');
            callback('account exists', null); 
        }
    });
};

var checkLogin_DB = function(emailInput, passwordInput, callback) {
    Account.findOne({email: emailInput}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('account dne', null);
        } else {
            var passwordDB = account.password;
            if (passwordInput == passwordDB) {
                callback(account, null); //send back the account object
            } else {
                callback('incorrect password', null);
            }
        }
    });
};

var accountdb = {
    createAccount: createAccount_DB,
    checkLogin: checkLogin_DB
}

module.exports = accountdb;