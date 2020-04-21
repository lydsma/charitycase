var express = require('express');
var router = express.Router();
var accountdb = require('../database/accountdb.js');

// get current user
router.get('/currentUser', function (req, res) {
  if (sess.email) {
    console.log('Current user is: ' + sess.email);
    res.send(sess.email);
  } else { //no one logged in
    res.send(null);
  }
});

// get signup page
router.get('/', function (req, res) {
  console.log('Loading signup page');
  res.render('signup.ejs', {
    message: null
  });
});

// get signup page
router.get('/signup', function (req, res) {
  console.log('Loading signup page');
  res.render('signup.ejs', {
    message: null
  });
});

// signup: create new account
router.post('/create', function (req, res) {
  var name = req.body.name;
  var email = req.body.email;
  var password = req.body.password;
  var accType = false;
  if (req.body.checkAccType == 'on') {
    accType = true;
  }

  sess = req.session;

  accountdb.createAccount(name, email, password, accType, function (results, err) {
    if (err) {
      console.log(err);
      res.render('signup.ejs', {
        message: err
      });
    } else if (email == "" || password == "" || name == "") {
      res.render('signup.ejs', {
        message: "Please fill out all input fields"
      });
    } else if (results == 'account exists') {
      res.render('signup.ejs', {
        message: "Account already exists"
      });
    } else {
      console.log('Created ' + results);
      console.log('Rendering home');
      sess.email = email;
      sess.fullname = name;
      sess.recipient = accType;
      console.log('Cookies set ' + sess.email + ' ' + sess.fullname);
      res.redirect('/home');
    }
  });
});

// login: check account credentials 
router.post('/login', function (req, res) {
  console.log('Checking current account');
  var email = req.body.email;
  var password = req.body.password;

  sess = req.session;

  accountdb.checkLogin(email, password, function (results, err) {
    if (err) {
      res.render('login.ejs', {
        message: err
      });
    } else if (email == "" || password == "") {
      res.render('login.ejs', {
        message: "Please fill out all input fields"
      });
    } else if (results == 'account dne') {
      res.render('login.ejs', {
        message: "User does not exist"
      });
    } else if (results == 'incorrect password') {
      res.render('login.ejs', {
        message: "Incorrect password for this user"
      });
    } else {
      console.log('Success, routing to home page');
      console.log(results); //should output account object 
      //save user email and name in session cookies
      sess.email = email;
      sess.fullname = results.name;

      accountdb.checkType(email, function (results, err) {
        if (err) {
          res.send({
            message: 'error'
          })
        } else {
          console.log(results)
          sess.type = results
          console.log('Cookies set ' + sess.email + ' ' + sess.fullname + ' ' + sess.type);
          req.session.save();
          console.log(req.session);

          res.redirect('/home');
        }
      })
    }
  });

});


// logout 
router.get('/logout', function (req, res) {
  req.session.destroy((err) => {
    if (err) {
      return console.log(err);
    }
    res.redirect('/');
  });
});


/********************** profile ***************************/

// get profile page
router.get('/profile', function (req, res) {
  var email = sess.email;
  var name = sess.fullname;
  var recipient = sess.recipient;

  if (email) {
    console.log('Loading profile page');
    res.render('profile.ejs', {
      nameMessage: name,
      emailMessage: email,
      accType: recipient
    });
  }
});

// load profile pic 
router.get('/profilepic', function (req, res) {
  var email = sess.email;
  accountdb.checkProfilePic(email, function (results, err) {
    if (err) {
      console.log('error saving');
      res.send(err);
    } else {
      res.send(results);
    }
  });
});

// save profile pic 
router.post('/updateprofilepic', function (req, res) {
  var link = req.body.profilepic;
  var email = sess.email;

  accountdb.changeProfilePic(email, link, function (results, err) {
    if (err) {
      console.log('error saving');
      res.send({
        err
      });
    } else {
      console.log(' results ' + results);
      res.send('success');
    }
  });
});

// edit: change name 
router.post('/changename', function (req, res) {
  email = sess.email;
  newName = req.body.nameInput; //profile input should be 'newName' 
  accountdb.changeName(email, newName, function (results, err) {
    if (newName == "") {
      res.render('edit.ejs', {
        namemessage: "Please fill out the field",
        pwmessage: null,
        changepw: false
      });
    }
    if (results) {
      console.log('new name ' + results);
      res.render('edit.ejs', {
        namemessage: "Name updated to " + results,
        pwmessage: null,
        changepw: false
      });
    }
  });
});

// edit: check before change pw 
router.post('/checkpw', function (req, res) {
  password = req.body.oldPassword; //profile input should be 'oldPassword'
  accountdb.checkLogin(sess.email, password, function (results, err) {
    if (err) {
      res.render('edit.ejs', {
        namemessage: null,
        pwmessage: err,
        changepw: false
      });
    } else if (password == "") {
      res.render('edit.ejs', {
        namemessage: null,
        pwmessage: "Please fill out the field",
        changepw: false
      });
    } else if (results == 'incorrect password') {
      res.render('edit.ejs', {
        namemessage: null,
        pwmessage: "Incorrect password for this user",
        changepw: false
      });
    } else {
      res.render('edit.ejs', {
        namemessage: null,
        pwmessage: "Please enter your new password:",
        changepw: true
      });
    }
  });
});

// edit: change pw 
router.post('/changepw', function (req, res) {
  password = req.body.newPassword;
  accountdb.changePassword(sess.email, password, function (results, err) {
    if (password == "") {
      res.render('edit.ejs', {
        namemessage: null,
        pwmessage: "Please fill out the field",
        changepw: false
      });
    }
    if (results) {
      console.log('new pw ' + results);
      res.render('edit.ejs', {
        namemessage: null,
        pwmessage: "Password updated!",
        changepw: true
      });
    }
  });
});

// edit: delete acct
router.get('/delete', function (req, res) {
  if (sess.email) {
    console.log('deleting ' + sess.email);
    accountdb.deleteAccount(sess.email, function (results, err) {
      if (err) {
        res.send(err);
      } else {
        console.log('deleted:' + results);
        res.render('login.ejs', {
          message: null
        });
      }
    });
  }
});

module.exports = router;