var express = require('express');
var router = express.Router();
var Person = require('../database/database.js');
//var db = require('../database/database.js'); 

/* Create new user */
router.post('/create', (req, res) => {
	// construct the Person from the form data which is in the request body
	var newPerson = new Person ({
		name: req.body.name,
		age: req.body.age,
	    });

	// save the person to the database
	newPerson.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		}
		else {
		    // display the "successfull created" page using EJS
			console.log('success');
			console.info('successs');
			res.render('created', {person : newPerson});
		}
	    } ); 
    }
  );

module.exports = router;
