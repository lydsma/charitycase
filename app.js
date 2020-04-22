var createError = require('http-errors')
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var bodyParser = require('body-parser');

// set up Express
var express = require('express');
var session = require('express-session');
var app = express();

app.set('view engine', 'ejs'); // set up EJS
app.set('views', path.join(__dirname, 'views')); // view engine setup
app.use(cookieParser());
app.use(session({
	secret: "initializepls",
	saveUninitialized: true,
	resave: false,
	cookie: {
		secure: false
	}
})); //initialize cookie session 
app.use(logger('dev'));
app.use(bodyParser.urlencoded({
	extended: true
}));
app.use(express.json());
app.use(express.urlencoded({
	extended: false
}));
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'node_modules')));
/*************************************************/
var sess; //global session var
var accountRouter = require('./routes/accountRouter');
var homeRouter = require('./routes/homeRouter');
var homedb = require('./database/homedb.js');


app.use('/public', express.static('public'));

const redirectLogin = (req, res, next) => {
	if (!req.session.email) {
		res.redirect('/');
	} else {
		next()
	}
}

const redirectHome = (req, res, next) => {
	if (req.session.email) {
		res.redirect('/home')
	} else {
		next()
	}
}

app.use(function(req, res, next) {
    var session_id = (req.body && req.body.sid) || req.query && req.query.sid
    req.sessionStore && req.sessionStore.get(session_id, function(err, session) {
        if (session) {
            // createSession() re-assigns req.session
            req.sessionStore.createSession(req, session)
        }
        return next()
    })
})

app.get('/', redirectHome, (req, res) => {
	res.render('login.ejs', {
		message: null
	});
});

// account routes
app.use('/account', accountRouter);

// home routes
app.use('/home', redirectLogin, homeRouter);

// get profile
app.get('/profile', function(req,res) {
	var email = req.session.email;
	var name = req.session.fullname;
	var accType = req.session.recipient;
	
	if (email) {
	  console.log('Loading profile page');
	  res.render('profile.ejs', {nameMessage: name, emailMessage: email, accType: accType}); 
	}
  });

// get editpage
app.get('/edit', function (req, res) {
	console.log('Loading edit page');
	res.render('edit.ejs', {namemessage: null, pwmessage: null, changepw: false}); 
  });
  
// get signup page
app.get('/signup', function (req, res) {
	console.log('Loading signup page');
	res.render('signup.ejs', {
	  message: null
	});
  });

/************************* MOBILE ***************************/

  app.get('/test', function(req,res){
	console.log('android calling');
	res.json({'name' : 'works'});
  });

 app.get('/mobilegethomeposts', function (req, res) {   // /home
	console.log('Load homepage');
	 homedb.getAllPosts(function(results, err) {
	  if (err) {
		console.log(err);
		res.json({'status':err});
	  } else if (results == 'no posts') {
		res.json({'status':'no posts'});
	  } else {
		console.log('sending ' + results);
	res.json({'posts': results});
	  }
	}); 
  });

/************************* MOBILE ***************************/


/**********************example code; delete later***************************/

// route for showing all the people
app.use('/all', (req, res) => {

	// find all the Person objects in the database
	Person.find({}, (err, persons) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
		} else {
			if (persons.length == 0) {
				res.type('html').status(200);
				res.write('There are no people');
				res.end();
				return;
			}
			// use EJS to show all the people
			res.render('all', {
				persons: persons
			});

		}
	}).sort({
		'age': 'asc'
	}); // this sorts them BEFORE rendering the results
});

// route for accessing data via the web api
// to use this, make a request for /api to get an array of all Person objects
// or /api?name=[whatever] to get a single object
app.use('/api', (req, res) => {
	console.log("LOOKING FOR SOMETHING?");

	// construct the query object
	var queryObject = {};
	if (req.query.name) {
		// if there's a name in the query parameter, use it here
		queryObject = {
			"name": req.query.name
		};
	}

	Person.find(queryObject, (err, persons) => {
		console.log(persons);
		if (err) {
			console.log('uh oh' + err);
			res.json({});
		} else if (persons.length == 0) {
			// no objects found, so send back empty json
			res.json({});
		} else if (persons.length == 1) {
			var person = persons[0];
			// send back a single JSON object
			res.json({
				"name": person.name,
				"age": person.age
			});
		} else {
			// construct an array out of the result
			var returnArray = [];
			persons.forEach((person) => {
				returnArray.push({
					"name": person.name,
					"age": person.age
				});
			});
			// send it back as JSON Array
			res.json(returnArray);
		}
	});
});

app.listen(80, function () {
	console.log('Running on port 80...');
})



module.exports = app;