var express = require('express');
var router = express.Router();
var homedb = require('../database/homedb.js');

router.get('/', function (req, res) {   // /home
    console.log('Load homepage');
    var name = req.session.fullname
    var email = req.session.email
    res.render('home.ejs', {name: name, email: email}); 
  });

router.post('/createpost', function(req, res) {
  var type = req.body.type;
  var category = req.body.category;
  var address = req.body.address;
  var tag = req.body.tag;
  var description = req.body.description;
  
  homedb.createPosts(type, category, address, tag, description, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      console.log('Created ' + results);
      res.json({'status':'success'});
    }
  });
});

router.get('/getallposts', function(req,res){
  homedb.getAllPosts(function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else if (results == 'no posts') {
      res.json({'status':'no posts'});
    } else {
      console.log('sending ' + results);
      res.json({'status':'success', 'posts' : results});
    }
  });
});

module.exports = router;