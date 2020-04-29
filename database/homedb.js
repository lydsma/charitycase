var mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect('mongodb+srv://dbUser350:mn%25Q3e5%24@cluster0-q1ukr.gcp.mongodb.net/test?retryWrites=true&w=majority', {
  useNewUrlParser: true,
  useUnifiedTopology: true
});
var db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
  console.info('Connected to home db!!');
});

var Schema = mongoose.Schema;

var postSchema = new Schema({ //for login
  sender: String,
  type: String,
  category: String,
  zip: Number,
  tags: String,
  description: String,
  comments: Array,
  identification: String,
});

postSchema.methods.standardizeName = function () {
  this.name = this.name.toLowerCase();
  return this.name;
}

var Post = mongoose.model('Posts', postSchema);

/**********************DB calls*************************/

var createPost_DB = function (senderInput, typeInput, categoryInput, zipInput, tags, description, comments, identification, callback) {
  var newPost = new Post({
    sender: senderInput,
    type: typeInput,
    category: categoryInput,
    zip: zipInput,
    tags: tags,
    description: description,
    comments: comments,
    identification: identification
  });

  newPost.save((err) => {
    if (err) {
      res.type('html').status(200);
      res.write('uh oh: ' + err);
      console.log(err);
      callback(null, err);
    } else {
      // display the "successfull created" page using EJS
      console.log('Successfully saved: ' + newPost);
      callback(newPost, null);
    }
  });
};

var getAllPosts_DB = function (callback) {
  Post.find((err, allPosts) => {
    if (err) {
      callback(null, err);
    } else if (allPosts.length == 0) {
      callback('no posts', null);
    } else {
      callback(allPosts, null); //allPosts is an array of objs 
    }
  });
};

var getFilteredPosts_DB = function (searchEntry, callback) {

  Post.find({
    sender: searchEntry
  }, (err, senders) => {
    if (err) {
      console.log(err);
    } else if (senders.length != 0) {
      console.log('senders callback')
      console.log(senders.length);
      callback(senders, null);
    } else {
      Post.find({
        type: searchEntry
      }, (err, types) => {
        if (err) {
          console.log(err);
        } else if (types.length != 0) {
          console.log('types callback')
          console.log(types)
          callback(types, null)
        } else {
          Post.find({
            category: searchEntry
          }, (err, categories) => {
            if (err) {
              console.log(err);
            } else if (categories.length != 0) {
              console.log('category callback')
              callback(categories, null)
            } else {
              Post.find({
                tags: searchEntry
              }, (err, tags) => {
                if (err) {
                  console.log(err);
                } else if (tags.length != 0) {
                  console.log('tag callback')
                  callback(tags, null)
                } else {
                  console.log('empty callback')
                  callback(null, null);
                }
              })
            }
          })
        }
      })
    }
  })

  // Post.find({zip: searchEntry}, (err, senders) => {
  //   if (err) {
  //     console.log(err);
  //   } else {
  //     results.push(senders)
  //   }
  // })
}

var getComments_DB = function(post, callback) {
  Post.findOne({identification: post}, (err, foundPost) => {
    if (err) {
      console.log(err)
      callback(null, err)
    } else {
      callback(foundPost.comments, null)
    }
  })
}

var addComment_DB = function(comment, post, callback) {
  Post.findOneAndUpdate({identification: post}, {$push: {comments: comment}}, function (err, result) {
      if (err) {
          callback(null, err);
      } else {
          callback(comment, null)
      }
  });
};

var getPostByID_DB = function(id, callback) {
  Post.findOne({identification: id}, (err, foundPost) => {
    if (err) {
      console.log(err)
      callback(null, err)
    } else {
      callback(foundPost, null)
    }
  })
}

var homedb = {
  createPosts: createPost_DB,
  getAllPosts: getAllPosts_DB,
  getFilteredPosts: getFilteredPosts_DB,
  getComments: getComments_DB,
  addComment: addComment_DB,
  getPostByID: getPostByID_DB,
}

module.exports = homedb;