var express = require('express');
var router = express.Router();
var accountdb = require('../database/accountdb.js');

// signup page
router.get('/', function (req, res) {
    console.log('Loading signup page');
    res.render('signup.ejs', {message: null}); 
  });

// signup page
router.get('/signup', function (req, res) {
    console.log('Loading signup page');
    res.render('signup.ejs', {message: null}); 
});

// save new account
router.post('/create', function(req, res) {
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;

    accountdb.createAccount(name, email, password, function(results, err) {
      if (err) {
        console.log(err);
        res.render('signup.ejs', {message: err});
      } else if (email == "" || password == "" || name == "") {
        res.render('signup.ejs', {message: "Please fill out all input fields"});
      } else if (results == 'account exists') {
        res.render('signup.ejs', {message: "Account already exists"});
      } else {
        console.log('Created ' + results);
        console.log('Rendering home');
        res.redirect('/home');
      }
    });
});

// login: check account credentials 
  router.post('/login', function (req, res) {
    console.log('Checking current account');
    var email = req.body.email;
    var password = req.body.password;

    accountdb.checkLogin(email, password, function(results,err) {
      if (err) {
        res.render('login.ejs', {message: err});
      } else if (email == "" || password == "") {
        res.render('login.ejs', {message: "Please fill out all input fields"});
      } else if (results == 'account dne') {
				res.render('login.ejs', {message: "User does not exist"});
      } else if (results == 'incorrect password') {
				res.render('login.ejs', {message: "Incorrect password for this user"});
      } else {
        console.log('Success, routing to home page');
        console.log(results); //should output account object 
        //save user email and name in session cookies
        //req.session.email = email;
        //req.session.userFullName = results.name;
        //res.render('home.ejs', {account: results});
        res.redirect('/home');
      }
    });

  });

  module.exports = router;