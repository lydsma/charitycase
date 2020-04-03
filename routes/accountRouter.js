var express = require('express');
var router = express.Router();
var accountdb = require('../database/accountdb.js');

// login page
router.get('/', function (req, res) {
    console.log('Loading signup page');
    res.redirect('/public/loginpage.html');
  });

// signup page
router.get('/signup', function (req, res) {
    console.log('Loading signup page');
    res.redirect('/public/personform.html');
});

// save new account
router.post('/create', function(req, res) {
    console.log('Creating new account');
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;

    accountdb.createAccount(name, email, password, function(results, err) {
      if (err) {
        console.log(err);
        res.redirect('/public/loginpage.html');
      } else {
        console.log('Rendering page');
        res.render('created', { account : results });
      }
    });
});

  module.exports = router;