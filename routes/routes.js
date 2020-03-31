var express = require('express');
var db = require('../database/database.js'); 

/* GET home page. */
var getHomePage = function (req, res, next) {
  //res.render('index', { title: 'Express' });
};

/* GET users listing. */
var getUsersListing = function (req, res, next) {
  res.send('respond with a resource');
};


var routes = {
  get_homepage: getHomePage,
  
}

module.exports = routes;
