var express = require('express');
var router = express.Router();
var async = require('async');
var homedb = require('../database/homedb.js');
var accountdb = require('../database/accountdb.js');  //so can get prof pics

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

        //get profile pic for each post 
        var finalResults = [];
        async.each(results, function(f, cb) {
          console.log('async ' + f.sender);
          accountdb.checkHomeProfilePic(f.sender, function (picLink, err) {
            if (err) {
              console.log('error');
            } else {
                if (picLink == '' || picLink == undefined || picLink == 'account dne') {
                  picLink = 'https://www.searchpng.com/wp-content/uploads/2019/02/Deafult-Profile-Pitcher.png';
                }
                var newPost = {
                  sender: f.sender,
                  type: f.type,
                  category: f.category,
                  zip: f.zip,
                  tags: f.tags,
                  description: f.description,
                  profilepic: picLink
                };
                finalResults.push(newPost);
                cb();
            }
          });
        }, function() {
            console.log(JSON.stringify(finalResults));
            res.render('home.ejs', {'name': name, 'email': email, 'posts': finalResults});
        }); //end async
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

router.get('/viewprofile', function(req,res){
  var name = "Jack McKnight";
  // var name = req.params.name;

  accountDB.getUser(name, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else if (results == 'account dne') {
      res.json({'status':'account dne'});
    } else {
      console.log('sending ' + results);
      var email = results.email;
      var recipient = results.recipient;

      accountdb.getAllWallPosts(name, function(results, err) {
        if (err) {
          console.log(err);
          res.json({'status':err});
        } else {
          console.log('sending ' + results);

          if (results == 'no wall posts') {
            console.log("no wall posts");
          }

          if (email) {
            console.log('Loading this users profile page');
            res.render('viewprofile.ejs', {nameMessage: name, emailMessage: email, accType: recipient, wallposts: results}); 
            }
        }
      }); 
  
      
    }
  })

  
});

router.post('/createwallpost', function(req, res) {
  var sender = req.session.fullname;
  var receiver = "Jack McKnight";
  var description = req.body.description;
  
  accountdb.createWallPost(sender, receiver, description, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      console.log('Created ' + results);
      res.redirect('/home/viewprofile');
    }
  });
});

module.exports = router;