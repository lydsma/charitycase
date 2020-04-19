var createError = require('http-errors')
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

// set up Express
var express = require('express');
var app = express();

// set up EJS
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
app.set('views', path.join(__dirname, 'views')); // view engine setup
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'node_modules')));

/*************************************************/

var accountRouter = require('./routes/accountRouter');

//app.all('/', (req, res) => { res.redirect('/public/loginpage.html'); } );	when i fix login page
app.all('/', (req, res) => { res.redirect('/public/personform.html'); } ); 

app.use('/public', express.static('public'));

// sign up and/or login 
app.use('/account', accountRouter);

/**********************class ex code; delete later***************************/

// import the Person class from Person.js
var Person = require('./database/Person.js');

// route for showing all the people
app.use('/all', (req, res) => {
    
	// find all the Person objects in the database
	Person.find( {}, (err, persons) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
		    if (persons.length == 0) {
			res.type('html').status(200);
			res.write('There are no people');
			res.end();
			return;
		    }
		    // use EJS to show all the people
		    res.render('all', { persons: persons });

		}
	    }).sort({ 'age': 'asc' }); // this sorts them BEFORE rendering the results
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
	    queryObject = { "name" : req.query.name };
	}
    
	Person.find( queryObject, (err, persons) => {
		console.log(persons);
		if (err) {
		    console.log('uh oh' + err);
		    res.json({});
		}
		else if (persons.length == 0) {
		    // no objects found, so send back empty json
		    res.json({});
		}
		else if (persons.length == 1 ) {
		    var person = persons[0];
		    // send back a single JSON object
		    res.json( { "name" : person.name , "age" : person.age } );
		}
		else {
		    // construct an array out of the result
		    var returnArray = [];
		    persons.forEach( (person) => {
			    returnArray.push( { "name" : person.name, "age" : person.age } );
			});
		    // send it back as JSON Array
		    res.json(returnArray); 
		}
		
	    });
    });

module.exports = app;

app.listen(3000,  () => {
	console.log('Listening on port 3000');
	});

/** 
 *

 * 
 * // server listener
app.listen(3000,  () => {
	console.log('Listening on port 3000');
	});
	
  app.use('/', function(req, res, next) {
  res.send('<strong>!!!!! success !!!!! </strong>');

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

}); */