var express = require('express');
var router = express.Router();
var homedb = require('../database/homedb.js');

router.get('/', function (req, res) {   // /home
    console.log('Load homepage');
    res.render('home.ejs', {message: null}); 
  });

module.exports = router;