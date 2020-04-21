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
  description: String
});

postSchema.methods.standardizeName = function () {
  this.name = this.name.toLowerCase();
  return this.name;
}

var Post = mongoose.model('Posts', postSchema);

/**********************DB calls*************************/

var createPost_DB = function (senderInput, typeInput, categoryInput, zipInput, tags, description, callback) {
  var newPost = new Post({
    sender: senderInput,
    type: typeInput,
    category: categoryInput,
    zip: zipInput,
    tags: tags,
    description: description
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
      callback(allPosts, null);
    }
  });
};

var homedb = {
  createPosts: createPost_DB,
  getAllPosts: getAllPosts_DB
}

module.exports = homedb;