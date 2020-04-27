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
    profilepic: String,
    followers: [String],
    following: [String],
});

accountSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}

var wallPostSchema = new Schema({
    sender: String,
    receiver: String,
    description: String
});

var Account = mongoose.model('Accounts', accountSchema);
var WallPost = mongoose.model('WallPosts', wallPostSchema);

/***********************DB calls**************************/

var createAccount_DB = function(nameInput, emailInput, passwordInput, accType, callback) {
    var newAcct = new Account ({
		name: nameInput,
        email: emailInput,
        password: passwordInput,
        recipient: accType,
        profilepic: '',
        followers: [],
        following: [],
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

var checkHomeProfilePic_DB = function(nameInput, callback) {
    Account.findOne({name: nameInput}, (err, account) => {
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

var getUser_DB = function(nameInput, callback) {
    Account.findOne({name: nameInput}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('account dne', null);
        } else {
            callback(account, null); //send back the account object
        }
    });
};

var createWallPost_DB = function (senderInput, receiverInput, description, callback) {
    var newWallPost = new WallPost({
      sender: senderInput,
      receiver: receiverInput,
      description: description
    });
  
    newWallPost.save((err) => {
      if (err) {
        res.type('html').status(200);
        res.write('uh oh: ' + err);
        console.log(err);
        callback(null, err);
      } else {
        // display the "successfull created" page using EJS
        console.log('Successfully saved: ' + newWallPost);
        callback(newWallPost, null);
      }
    });
  };

var getAllWallPosts_DB = function (nameInput, callback) {
    WallPost.find({receiver: nameInput}, (err, allWallPosts) => {
        if (err) {
            callback(null, err);
        } else if (allWallPosts.length == 0) {
            callback('no wall posts', null);
        } else {
            callback(allWallPosts, null);
        }
    });
};

var addFollower_DB = function (follower, user, callback) {
    Account.findOne({name: user}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('user dne', null);
        } else {
            if(!account.followers.includes(follower)) {
                account.followers.push(follower);
                account.save();
            }

            console.log("user's account: " + account);
            console.log("user's followers: " + account.followers);
        }
    });

    Account.findOne({name: follower}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('follower dne', null);
        } else {
            if(!account.following.includes(user)) {
                account.following.push(user);
                account.save();
                callback(account, null);
            }

            console.log("follower's account: " + account);
            console.log("follower's following: " + account.following);
        }
    });
};

var removeFollower_DB = function (follower, user, callback) {
    Account.findOne({name: user}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('user dne', null);
        } else {
            if(account.followers.includes(follower)) {
                // account.followers = account.followers.filter(e => e !== follower);

                var index = account.followers.indexOf(follower);    // <-- Not supported in <IE9
                if (index !== -1) {
                    account.followers.splice(index, 1);
                }
                account.save();
            }
            
            console.log("user's account: " + account);
            console.log("user's followers: " + account.followers);
        }
    });

    Account.findOne({name: follower}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('follower dne', null);
        } else {
            if(account.following.includes(user)) {
                // account.following = account.following.filter(e => e !== user);

                var index = account.following.indexOf(user);    // <-- Not supported in <IE9
                if (index !== -1) {
                    account.following.splice(index, 1);
                }
                account.save();
                callback(account, null);
            }

            console.log("follower's account: " + account);
            console.log("follower's following: " + account.following);
        }
    });
};

var checkIfFollowing_DB = function (follower, user, callback) {
    Account.findOne({name: follower}, (err, account) => {
        if (err) {
            callback(null, err);
        } else if (!account) {
            callback('user dne', null);
        } else {
            callback(account.following.includes(user), null);
        }
    });
};

var accountdb = {
    createAccount: createAccount_DB,
    checkLogin: checkLogin_DB,
    changeName: changeName_DB,
    changePassword: changePassword_DB,
    checkProfilePic: checkProfilePic_DB,
    checkHomeProfilePic: checkHomeProfilePic_DB,
    changeProfilePic: changeProfilePic_DB,
    deleteAccount: deleteAccount_DB,
    checkType: checkType_DB,
    getUser: getUser_DB,
    createWallPost: createWallPost_DB,
    getAllWallPosts: getAllWallPosts_DB,
    addFollower: addFollower_DB,
    removeFollower: removeFollower_DB,
    checkIfFollowing: checkIfFollowing_DB,
}

module.exports = accountdb;