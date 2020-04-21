var mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect('mongodb+srv://dbUser350:mn%25Q3e5%24@cluster0-q1ukr.gcp.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true });
mongoose.set('useFindAndModify', false);
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
    password: String,
    recipient: Boolean,
    profilepic: String
});

accountSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}

var Account = mongoose.model('Accounts', accountSchema);

/***********************DB calls**************************/

var createAccount_DB = function(nameInput, emailInput, passwordInput, accType, callback) {
    var newAcct = new Account ({
		name: nameInput,
        email: emailInput,
        password: passwordInput,
        recipient: accType,
        profilepic: ''
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

var changeName_DB = function(emailInput, nameInput, callback) {
    Account.findOneAndUpdate({email: emailInput}, {name: nameInput}, {new: true}, function (err, result) {
        if (err) {
            callback(null, err);
        } else {
            console.log(nameInput);
            console.log(result.name);
            if (nameInput == result.name) {
                callback(result.name,null);
            };
        }
    });
};

var changePassword_DB = function(emailInput, pwInput, callback) {
    Account.findOneAndUpdate({email: emailInput}, {password: pwInput}, {new: true}, function (err, result) {
        if (err) {
            callback(null, err);
        } else {
            if (pwInput == result.password) {
                callback(result.password,null);
            };
        }
    });
};

var checkProfilePic_DB = function(emailInput, callback) {
    Account.findOne({email: emailInput}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('account dne', null);
        } else {
            callback(account.profilepic, null);
        }
    });
};

var checkType_DB = function(emailInput, callback) {
    Account.findOne({email: emailInput}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('account dne', null);
        } else {
            console.log('account type = ' + account.recipient)
            if (account.recipient) {
                callback('recipient', null);
            } else {
                callback('donor', null);
            }
        }
    });
};

var changeProfilePic_DB = function(emailInput, newProfilePic, callback) {
    Account.findOneAndUpdate({email: emailInput}, {profilepic: newProfilePic}, {new: true}, function (err, result) {
        if (err) {
            callback(null, err);
        } else {
            if (newProfilePic == result.profilepic) {
                console.log('db ' + result.profilepic);
                callback(result.profilepic,null);
            };
        }
    });
};

var deleteAccount_DB = function(emailInput, callback) {
    Account.deleteOne({email: emailInput}, function(err) {
        if (err) {
            console.log(err);
            callback(null,err);
        } else {
            console.log('success');
            callback(emailInput, null);
        }
    });
};

var accountdb = {
    createAccount: createAccount_DB,
    checkLogin: checkLogin_DB,
    changeName: changeName_DB,
    changePassword: changePassword_DB,
    checkProfilePic: checkProfilePic_DB,
    changeProfilePic: changeProfilePic_DB,
    deleteAccount: deleteAccount_DB,
    checkType: checkType_DB,
}

module.exports = accountdb;