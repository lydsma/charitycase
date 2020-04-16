var mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect('mongodb+srv://dbUser350:mn%25Q3e5%24@cluster0-q1ukr.gcp.mongodb.net/test?retryWrites=true&w=majority', { useNewUrlParser: true, useUnifiedTopology: true });
var db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.info('Connected to home db');
});

// ref to accountdb.js --> accountRouter.js --> app.js //

var homedb = {
}

module.exports = homedb;

var Schema = mongoose.Schema;

var postSchema = new Schema({    //for login
	  type: String, 
    category: String,
    address: String,
    tags: String,
    description: String
});

postSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}

var Post = mongoose.model('Posts', accountSchema);
