var express = require('express');
var router = express.Router();
var homedb = require('../database/homedb.js');

router.get('/', function (req, res) {   // /home
    console.log('Load homepage');
    var name = req.session.fullname
    var email = req.session.email
    homedb.getAllPosts(function(results, err) {
      if (err) {
        console.log(err);
        res.json({'status':err});
      } else if (results == 'no posts') {
        res.json({'status':'no posts'});
      } else {
        console.log('sending ' + results);
        res.render('home.ejs', {'name': name, 'email': email, 'posts': results});
      }
    }); 
  });

router.post('/createpost', function(req, res) {
  var sender = req.session.fullname;
  var type = req.session.type;
  var category = req.body.category;
  var zip = req.body.zip;
  var tag = req.body.tag;
  var description = req.body.description;
  
  homedb.createPosts(sender, type, category, zip, tag, description, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      console.log('Created ' + results);
      res.redirect('/home')
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