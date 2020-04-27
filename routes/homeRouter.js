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

router.post('/filter', function(req, res) {
  var searchEntry = ("" + req.body.seq);
  console.log('search entry = ' + searchEntry)
  homedb.getFilteredPosts(searchEntry, function (results, err) {
    if (err) {
      console.log(err);
    } else {
      console.log('results = ' + results);
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
         //res.render('home.ejs', {'name': name, 'email': email, 'posts': finalResults});
          res.send({'results': finalResults});
      });
      //res.send({'results': results})
    }
  }) 
})


/**router.post('/filter', function(req, res) {
  var searchEntry = ("" + req.body.seq);
  console.log('search entry = ' + searchEntry)
  homedb.getFilteredPosts(searchEntry, function (results, err) {
    if (err) {
      console.log(err);
    } else {
      console.log('results = ' + results);
      res.send({'results': results})
    }
  }) 
})*/


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

router.get('/viewprofile/*', function(req,res){
  var url = req.url;
  var query = url.substring(url.indexOf(':') + 1);
  var name = query.replace("%20", " ");
  name = name.replace("+", " ");
  if (name == null) {
    res.redirect('/');
  }
  console.log("trying to access profile of: " + name);

  accountdb.getUser(name, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else if (results == 'account dne') {
      res.json({'status':'account dne'});
      console.log("account dne");
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
            console.log('Loading ' + name + 's profile page');
            res.render('viewprofile.ejs', {nameMessage: name, emailMessage: email, accType: recipient, wallposts: results});
            }
        }
      }); 
    }
  })

  
});

router.post('/createwallpost/*', function(req, res) {
  var sender = req.session.fullname;
  var url = req.url;
  var query = url.substring(url.indexOf(':') + 1);
  var receiver = query.replace("%20", " ");
  receiver = receiver.replace("+", " ");
  if (receiver == null) {
    res.redirect('/');
  }
  var description = req.body.description;
  
  accountdb.createWallPost(sender, receiver, description, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      console.log('Created ' + results);
      res.redirect('/home/viewprofile/:' + receiver);
    }
  });
});

router.post('/addfollower', function(req, res) {
  var follower = req.session.fullname;
  var user = req.body.user;

  // var url = req.url;
  // var query = url.substring(url.indexOf('=') + 1);
  // var user = query.replace("%20", " ");
  
  accountdb.addFollower(follower, user, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      console.log(follower + " followed " + user);
    }
  });
});

router.post('/removefollower', function(req, res) {
  var follower = req.session.fullname;
  var user = req.body.user;

  // var url = req.url;
  // var query = url.substring(url.indexOf('=') + 1);
  // var user = query.replace("%20", " ");
  
  accountdb.removeFollower(follower, user, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      console.log(follower + " unfollowed " + user);
    }
  });
});

router.get('/checkfollower', function(req, res) {
  var follower = req.session.fullname;
  // var user = req.body.user;

  var url = req.url;
  var query = url.substring(url.indexOf('=') + 1);
  var user = query.replace("%20", " ");
  user = user.replace("+", " ");

  accountdb.checkIfFollowing(follower, user, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else {
      if (results == true) {
        console.log(follower + " follows " + user);
      } else if (results == false) {
        console.log(follower + " does not follow " + user);
      } else {
        console.log("we don't know if " + follower + " follows " + user);
      }
      
      res.send(results);
    }
  });
});

router.get('/viewfollowers/*', function(req,res){
  var url = req.url;
  var query = url.substring(url.indexOf(':') + 1);
  var name = query.replace("%20", " ");
  name = name.replace("+", " ");
  if (name == null) {
    res.redirect('/');
  }
  console.log("trying to access followers of: " + name);

  accountdb.getUser(name, function(results, err) {
    if (err) {
      console.log(err);
      res.json({'status':err});
    } else if (results == 'account dne') {
      res.json({'status':'account dne'});
      console.log("account dne");
    } else {
      console.log('sending follower data of ' + results);
      var followers = results.followers;
      var following = results.following;
      res.render('viewfollowers.ejs', {
        nameMessage: name, 
        followersList: followers, 
        followingList: following});
    }
  })

  
});

module.exports = router;