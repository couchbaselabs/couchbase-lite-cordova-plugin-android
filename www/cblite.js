var exec = require('cordova/exec');
var PLUGIN_NAME = 'CBLite';

/*
 *  cblite - objects provide main API for Couchbase Lite integration 
*/
var cblite = function () { }

/* The fromBasicAuthentication is an authenticator that will authenticate using
 * HTTP Basic auth with the given username and password. This should only be 
 * used over an SSL/TLS connection, as otherwise it's very easy for anyone
 * sniffing network traffic to read the password.  
 * 
 * @param username
 *          {string} user to authenticate with - used with BasicAuthentication 
 * @param password
 *          {string} password to authenticate with - used with BasicAuthentication
*/
cblite.prototype.BasicAuthenticator = function(username, password) {
    var ba = new Object();
    ba.username = username;
    ba.password = password;
    ba.authType = "Basic";
    return ba;
};

/* closeDatabase - Close database synchronously. Before closing the database,
 * the active replicators, listeners and live queries will be stopped. 
 * 
 * @param dbName 
 *          {string} name of the database to open or create 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.closeDatabase = function (dbName, successCallback, errorCallback) {
	exec(successCallback, errorCallback, PLUGIN_NAME, 'closeDatabase', [dbName]);
};

/* copyDatabase - Copies a canned database from the given path to a new 
 * database with the given name and the configuration. The new database will be
 * created at the directory specified in the configuration. Without given the
 * database configuration, the default configuration that is equivalent to
 * setting all properties in the configuration to nil will be used. 
 * 
 * @param fromPath 
 *          {string} the source database path of the prebuilt database.
 * @param dbName 
 *          {string} name of the database to create from existing database
 * @param newConfig
 *          {DatabaseConfiguration} JSON object with new database
 *  		directory and encryptionKey properties.
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.copyDatabase = function (fromPath, dbName, newConfig, successCallback, errorCallback) {
	let defaultConfig = {
		fromPath: fromPath,
		dbName: dbName,
		newConfig: newConfig
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'copyDatabase', [defaultConfig]);
};

/* createFTSIndex - Create a full-text search index for full-text search query
 * with the match operator.
 *
 * @param dbName 
 *          {string} name of the database 
 * @param indexName 
 *          {string} name of the index to create 
 * @param ignoreAccents 
 *          {bool} Set to true ignore accents/diacritical marks. The default
 *  		value is false.
 * @param language 
 *          {string} The language code which is an ISO-639 language code such
 *  		as “en”, “fr”, etc. Setting the language code affects how word
 *  		breaks and word stems are parsed. Without setting the language
 *  		code, the current locale’s language will be used. Setting “” value
 *  		to disable the language features.
 * @param indexExpressions 
 *          {[string]} array of strings with what index expressions to add 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.createFTSIndex = function(dbName, indexName, ignoreAccents, language, indexExpressions, successCallback, errorCallback) {
	if (dbName == null || dbName == "" || indexName == null || indexName == "" || indexExpressions == null){
		throw ('error: dbName, indexName and index must have value');
	}

	let args = {
		dbName: dbName,
		indexName: indexName, 
		indexExpressions: indexExpressions,
		language: language,
		ignoreAccents: ignoreAccents
	};

	exec(successCallback, errorCallback, PLUGIN_NAME, 'createFTSIndex', [args]);
};

/* createOrOpenDatabase - Initializes a Couchbase Lite database with a given
 * name and database options. If the database does not yet exist, it will be
 * created. 
 * 
 * @param dbName 
 *          {string} name of the database to open or create 
 * @param config 
 *          {DatabaseConfiguration} JSON object with dbName, directory, and encryptionKey
 *  		properties. 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.createOrOpenDatabase = function (dbName, config, successCallback, errorCallback) {
	if (config == null)
	{
		config = new Object();
	}
	config.dbName = dbName;
	exec(successCallback, errorCallback, PLUGIN_NAME, 'createOrOpenDatabase', [config]);
};

/* createValueIndex - Create an index for regular queries. 
 *
 * @param dbName 
 *          {string} name of the database 
 * @param indexName 
 *          {string} name of the index to create 
 * @param indexExpressions
 *          {[string]} array of strings with what index expressions to add 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.createValueIndex = function(dbName, indexName, indexExpressions, successCallback, errorCallback) {
	if (dbName == null || dbName == "" || indexName == null || indexName == "" || indexExpressions == null){
		throw ('error: dbName, indexName and index must have value');
	}
	let args = {
		dbName: dbName,
		indexName: indexName, 
		indexExpressions: indexExpressions
	};

	exec(successCallback, errorCallback, PLUGIN_NAME, 'createValueIndex', [args]);
};

/* DatabaseConfiguration - helper method to get database info for arguments 
 *
 * @param dbName 
 *          {string} name of the database 
 * @param config 
 *          {object} object with directory and encryptionKey properties 
*/
cblite.prototype.DatabaseConfiguration = function (config) {

	var obj = new Object();
	//if config is passed in add values
	if (config != null) {
		if (config.hasOwnProperty('encryptionKey')) {
			obj.encryptionKey = config.encryptionKey;
		} else {
			obj.encryptionKey = ""; 
		}
		if (config.hasOwnProperty('directory')) {
			obj.directory = config.directory;
		} else {
			obj.directory = ""; 
		}
	} else {
		obj.directory = "";
		obj.encryptionKey = "";
	}
	return obj;
};

/* databaseExists - Checks if database exists in the given directory path
 *
 * @param dbName 
 *          {string} name of the database to open or create*
 * @param config
 *          {DatabaseConfiguration} JSON object with directory property.
 * @param successCallback
 *          {callback} javascript function to call if native code is successful
 * @param errorCallback
 *          {callback} javascript function to call if error happens in native
 *  		code
*/

cblite.prototype.databaseExists = function (dbName, config, successCallback, errorCallback) {	
	config.dbName = dbName;
	exec(successCallback, errorCallback, PLUGIN_NAME, 'databaseExists', [config]);
};

/* dbAddListener - Adds a database change listener. Changes will be posted on
 * the main thread(android)/queue(ios). 
 *
 * @param dbName 
 *          {string} name of the database to use 
 * @param callbackName
 *          {callback} javascript function to call when change listener post
 *  		changes 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native *
 * 			code 
*/
cblite.prototype.dbAddListener = function(databaseName, callbackName, successCallback, errorCallback) {
	if (databaseName == null || callbackName == null) {
		throw ("error: database name and callbackName can't be null");
	}
	let args = {
		dbName: databaseName,	
		jsCallback: callbackName
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'addChangeListener', [args]);
};

/* dbRemoveListener - Removes a database change listener 
 *
 * @param dbName 
 *          {string} name of the database to use 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.dbRemoveListener = function(databaseName, successCallback, errorCallback) {
	if (databaseName == null) {
		throw ("error: database name can't be null");
	}
	let args = {
		dbName: databaseName	
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'removeChangeListener', [args]);
};

/* deleteDatabase - Close and delete the database synchronously. Before closing
 * the database, the active replicators, listeners and live queries will be
 * stopped. 
 * 
 * @param dbName 
 *          {string} name of the database to delete 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.deleteDatabase = function(dbName, successCallback, errorCallback) {
	exec(successCallback, errorCallback, PLUGIN_NAME, 'deleteDatabase', [dbName]);
};

/* deleteDocument -  delete a document in the database. When write operations are 
 * executed concurrently, the last writer will overwrite all other written values.  
 *
 * @param id 
 *          {string} id/key of the document to remove from the database 
 * @param dbName 
 *          {string} name of the database to retrieve document from 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.deleteDocument = function (id, dbName, successCallback, errorCallback) {

	try {
		let args = this.getDocumentInfo(id, dbName);
		exec(successCallback, errorCallback, PLUGIN_NAME, 'deleteDocument', [args]);
	}
	catch (e) {
		throw e;
	}
};

/* deleteIndex - delete an index 
 *
 * @param dbName 
 *          {string} name of the database 
 * @param indexName 
 *          {string} name of the index to delete 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.deleteIndex = function(dbName, indexName, successCallback, errorCallback) {
	if (indexName == null || indexName == ""){
		throw ('error: dbName, indexName must have value');
	}
	let args = {
		dbName: dbName,
		indexName: indexName
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'deleteIndex', [args])
};

/* Domain enum
*/
cblite.prototype.Domain = {
	"DATABASE": "database",
	"QUERY": "query",
	"REPLICATOR": "replicator",
	"NETWORK": "network",
	"LISTENER": "listener",
	"ALL": "all"
};

/* enableConsoleLogging - Log allows to configure console and file logger or to set a 
 * custom logger.  Very useful for debugging.
 *
 * @param domain 
 *           {string} Log domain options that can be enabled in the console
 *  		logger.
 * @param logLevel 
 *           {string} Log level 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.enableConsoleLogging = function (domain, logLevel, successCallback, errorCallback) {
	if (domain == null || logLevel == null) {
		throw ('error: domain can not be null');
	}
	let args = {
		domain: domain,
		logLevel: logLevel
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'enableConsoleLogging', [args])
};

/* getBlob - get a Blob object for the given metadata. 
 *
 * @param dbName 
 *          {string} name of the database to retrieve document from 
 * @param blobMetadata
 *          {string} string of JSON metadata used to retrieve blob 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.getBlob = function(dbName, blobMetadata, successCallback, errorCallback){
	if (dbName == null) { 
		throw "error: dbName must be passed in to retrieve blob";
	} else if (blobMetadata == null) { 
		throw "error:  blobMetadata must be passed in to retrieve blob";
	} else {
		let args = {
			dbName: dbName,
			blobData: blobMetadata
		}
		exec(successCallback, errorCallback, PLUGIN_NAME, 'getBlob', [args]);
	}
};

/* getDocument - Gets a Document object with the given ID. 
 *
 * @param id 
 *          {string} id/key of the document to retrieve from the database 
 * @param dbName 
 *          {string} name of the database to retrieve document from 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.getDocument = function (id, dbName, successCallback, errorCallback) {
	try {
		let args = this.getDocumentInfo(id, dbName);
		exec(successCallback, errorCallback, PLUGIN_NAME, 'getDocument', [args]);
	} catch (e) {
		throw e;
	}
};

/* getDocumentFullInfo - helper method to get document info for arguments 
 *
 * @param id 
 *          {string} key/id of the document 
 * @param document 
 *          {string} string JSON of document 
 * @param dbName 
 *          {string} name of the database 
*/
cblite.prototype.getDocumentFullInfo = function (id, document, dbName) {
	if (id == null) {
		throw ('error: Document - id can not be null');
	}
	if (document == null) {
		throw ('error: Document - document can not be null');
	}
	if (dbName == null) {
		throw ('error: Document - dbName can not be null')
	}

	let args = {
		id: id,
		document: document,
		dbName: dbName
	};

	return args;
};

/* getDocumentInfo - helper method to get document info for arguments 
 *
 * @param id 
 *          {string} key/id of the document 
 * @param dbName 
 *          {string} name of the database 
*/
cblite.prototype.getDocumentInfo = function (id, dbName) {
	if (id == null) {
		throw ('error: Document - id can not be null');
	}
	if (dbName == null) {
		throw ('error: Document - dbName can not be null')
	}

	let args = {
		id: id,
		dbName: dbName
	};

	return args;
};

/* getEmbeddedResourcePath - get's native file system path to embedded resource - helper method, no 
 * cblite mapping to native api, more convince to Cordova developers 
 *
 * @param resourceName 
 *          {string} name of the embedded resource where the database is stored 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.getEmbeddedResourcePath = function (resourceName, successCallback, errorCallback){
	if (resourceName == null){
		throw ("error: database name can't be null");
	}
	let args = new Object();
	args.resourceName = resourceName;

	exec(successCallback, errorCallback, PLUGIN_NAME, 'getEmbeddedResourcePath', [args]);
};

/* getIndexes - return a list of indexes for a given database 
 *
 * @param dbName 
 *          {string} name of the database 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.getIndexes = function(dbName, successCallback, errorCallback){
	if (dbName == null || dbName == ""){
		throw ('error: dbName, indexName and index must have value');	
	}

	let args = {
		dbName: dbName,
	};

	exec(successCallback, errorCallback, PLUGIN_NAME, 'getIndexes', [args]);
};

/* LogLevel - level of logging enum
*/
cblite.prototype.LogLevel = {
	"DEBUG": "debug",
	"VERBOSE": "verbose",
	"INFO": "info",
	"WARNING": "warning",
	"ERROR": "error",
	"NONE": "none"
};

/* mutableDocumentSetString - updates an existing document with a key/value
 * pair property that is of string value.  This is similar to the
 * MutableDocument setString Native API call.
 *
 * @param id 
 *          {string} id/key of the document to update
 * @param key 
 *          {string} string of key (property) to update in the document
 * @param value 
 *          {string} string of value of the property to update in the document
 * @param dbName 
 *          {string} name of the database to get document from 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.mutableDocumentSetString = function (id, key, value, dbName, successCallback, errorCallback) {
	if (key == null || value == null) {
		throw ('error: key/value can not be null');
	}
	if (id == null) {
		throw ('error: id can not be null');
	}
	if (dbName == null) {
		throw ('error: dbName can not be null');
	}
	let args = {
		id: id,
		key: key,
		value: value,
		dbName: dbName
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'mutableDocumentSetString', [args]);
};

/* query - N1QL query to execute against the database and return results. 
 *
 * @param dbName 
 *          {string} name of the database to query 
 * @param query 
 *          {string} N1QL query to execute 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.query = function(dbName, query, successCallback, errorCallback) { 
	if (dbName == null || dbName == "" || query == null || query == "") {
		throw ('error: dbName or query is not set, must send dbName and a N1QL query to process');
	}
	let args = {
		dbName: dbName,
		query: query
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'queryDb', [args])
};

/* queryAddListener - Adds a query change listener. Changes will be posted on
 * the main thread(android)/queue(ios).
 *
 * @param databaseName 
 *          {string} name of the database 
 * @param query 
 *          {string} N1QL query to attach listener to
 * @param callbackName 
 *          {callback} javascript function to call when changes are posted.  
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.queryAddListener = function(databaseName, query, callbackName, successCallback, errorCallback) {
	if (databaseName == null || query == null || callbackName == null) {
		throw ("error: database name, query, or callbackName can't be null");
	}
	let args = {
		dbName: databaseName,	
		jsCallback: callbackName,
		query: query
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'queryAddChangeListener', [args]);
};

/* queryRemoveListener - Removes a query change listener. 
 * @param databaseName 
 *          {string} name of the database 
 * @param query 
 *          {string} N1QL query to remove listener
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.queryRemoveListener = function(dbName, query, successCallback, errorCallback) {
	if (query == null) {
		throw ("error: query name can't be null");
	}
	let args = {
	  dbName : dbName,
	  query: query
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'queryRemoveChangeListener', [args]);
};

/** Replicator - A replicator for replicating document changes between a local database and a target database. 
 * The replicator can be bidirectional or either push or pull. The replicator can also be one-short or continuous. The replicator 
 * runs asynchronously, so observe the status property to be notified of progress.
 *
 * @param config 
 *          {ReplicatorConfiguration} The configuration  
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.Replicator = function(config, success, fail){
	if (config == null) {
		throw ("error: config can't be null");
	}
	var that = this;
	that.replicatorConfig = config;
	that.replicatorHash = null;

	/** createReplicator - creates a replicator and returns the Hash Code that can be used i
	 * the start, stop, addChangeListener, and removeChangeListener functions.  This hash code
	 * is required in order to support multiple replicators on the same database but with different
	 * configuration options.  The hash code is returned in the successCallback as a JSON string.
	 *
	 * @param successCallback
	 *          {callback} javascript function to call if native code is successful
	 * @param errorCallback
	 *          {callback} javascript function to call if error happens in native
	 *  		code
	*/
	var createReplicator = function(config, successCallback, errorCallback){
		exec(successCallback, errorCallback, PLUGIN_NAME, 'replicator', [config]);
	}

	createReplicator(config, function(result) {
		var result = JSON.parse(result); // parse result to get replicatorHash
		that.replicatorHash = result.data;
		success('OK');
	}, function(err) {
		that.replicatorHash = null;
		fail(err);
	});

	/** addChangeListener - Adds a replication change listener. Changes will be 
 	 * posted on the main thread(android)/queue(ios).
	 *
	 * @param callbackName 
	 *          {callback} javascript function to call when changes are posted.  
	 * @param successCallback 
	 *          {callback} javascript function to call if native code is successful 
	 * @param errorCallback 
	 *          {callback} javascript function to call if error happens in native
	 *  		code 
	*/
	this.addChangeListener = function(callbackName, successCallback, errorCallback) {
		if (that.replicatorHash == null || callbackName == null) {
			throw ("error: hash and callbackName can't be null");
		}
		let args = {
			hash: that.replicatorHash, 
			jsCallback: callbackName
		};
		exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorAddChangeListener', [args]);
	};

	/** removeChangeListener - removes the replication change listener. 
	 *
	 * @param successCallback 
	 *          {callback} javascript function to call if native code is successful 
	 * @param errorCallback 
	 *          {callback} javascript function to call if error happens in native
	 *  		code 
	*/
	this.removeChangeListener = function(successCallback, errorCallback) {
		if (that.replicatorHash == null) {
			throw ("error: hash can't be null");
		}
		let args = {
			hash: that.replicatorHash
		};
		exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorRemoveChangeListener', [args]);
	};

	/** start - Starts the replicator. This method returns immediately; the replicator runs asynchronously and will report its 
	* progress through the replicator change notification.
	*
 	* @param successCallback 
 	*          {callback} javascript function to call if native code is successful 
 	* @param errorCallback 
 	*          {callback} javascript function to call if error happens in native code 
	*/
	this.start = function(successCallback, errorCallback) {
		if (that.replicatorHash == null) {
			throw ("error: hash can't be null");
		}
		let args = {
			hash: that.replicatorHash
		};
		exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStart', [args]);
	};

	/** stop - Stops a running replicator. This method returns immediately; 
 	* when the replicator actually stops, the replicator will change its status’s 
 	* activity level to .stopped and the replicator change notification will be 
 	* notified accordingly.
 	* @param replicatorHash 
	*          {replicatorHash} hash code returned from createReplicator function	
 	* @param successCallback 
 	*          {callback} javascript function to call if native code is successful 
 	* @param errorCallback 
 	*          {callback} javascript function to call if error happens in native code 
	*/
	this.stop = function(successCallback, errorCallback) {
		if (that.replicatorHash == null) {
			throw ("error: hash can't be null");
		}
		let args = {
			hash: that.replicatorHash
		};
		exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStop', [args]);
	};
  
  return this;
};

/**  
 *  Replicator configuration. 
 * @param databaseName
 *      {string} The local database name to replicate with the 
 * 		replication target. 
 * @param targetUrl
 *      {string} url of the replication target to replicate with. 
*/
cblite.prototype.ReplicatorConfiguration = function (databaseName, targetUrl){
	var obj = new Object();

    obj.databaseName = databaseName;
    obj.target = targetUrl;

	obj.heartbeat = 0;
	obj.replicatorType = this.ReplicatorType.PUSH_AND_PULL;
    obj.continuous = true; 
    obj.authenticator = {}; 
    obj.acceptOnlySelfSignedServerCertificate = false; 
    obj.pinnedServerCertificate = ""; 
    obj.headers = [];
    obj.channels = [];
    obj.documentIds = []; 
    obj.allowReplicatingInBackground = false; 

	return obj;
};

/* @deprecated - DO NOT USE 
 * replicatorStart - Initializes a replicator with the given configuration.  The
	* replicator is used for  replicating document changes between a local database and 
	* a target database. The replicator can be bidirectional or either push or pull. The
* replicator can also be one-short or continuous. The replicator runs asynchronously, 
* so observe the status property to be notified of progress.
*
* @param config 
*          {ReplicatorConfiguration} The configuration 
* @param successCallback 
*          {callback} javascript function to call if native code is successful 
* @param errorCallback 
*          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.Replicator.replicatorStart = function(config, successCallback, errorCallback) {
	if (config == null) {
		throw ("error: config can't be null");
	}
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStart', [config]);
};

/* @deprecated - DO NOT USE 
 * replicationAddListener - Adds a replication change listener. Changes will be 
 * posted on the main thread(android)/queue(ios).
 *
 * @param config 
 *          {ReplicatorConfiguration} The configuration 
 * @param callbackName 
 *          {callback} javascript function to call when changes are posted.  
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.replicatorAddListener = function(config, callbackName, successCallback, errorCallback) {
	if (config == null || callbackName == null) {
		throw ("error: config and callbackName can't be null");
	}
	let args = {
		config: config, 
		jsCallback: callbackName
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorAddChangeListener', [args]);
};

/* @deprecated - DO NOT USE 
 * replicatorRemoveListener - removes the replication change listener. 
 *
 * @param config 
 *          {ReplicatorConfiguration} The configuration 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.replicatorRemoveListener = function(config, successCallback, errorCallback) {
	if (databaseName == null) {
		throw ("error: config can't be null");
	}
	let args = {
		dbName: databaseName	
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorRemoveChangeListener', [config]);
};

cblite.prototype.ReplicatorType = { 
	"PUSH_AND_PULL": "PUSHANDPULL", 
	"PUSH": "PUSH", 
	"PULL": "PULL"
};

/* @deprecated - DO NOT USE 
 * replicatorStop - Stops a running replicator. This method returns immediately; 
 * when the replicator actually stops, the replicator will change its status’s 
 * activity level to .stopped and the replicator change notification will be 
 * notified accordingly.
 *
 * @param config 
 *          {ReplicatorConfiguration} The configuration 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.replicatorStop = function(config, successCallback, errorCallback) {
	if (config == null) {
		throw ("error: config can't be null");
	}
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStop', [config]);
};

/* saveBlob - Saves blob on the database.  NOTE: 
 * successCallback returns metadata that must be used to retrieve the blob
 * using the getBlob function.  It's very important that you keep a reference
 * to the metadata if you need to retrieve the blob. 
 *
 * @param databaseName 
 *          {string} name of the database to retrieve document from 
 * @param contentType 
 *          {string} type of blob data you are storing  
 * @param blobData
 *          {string} base64 UTF-8 encoded data to store as a blob 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.saveBlob = function (databaseName, contentType, blobData, successCallback, errorCallback) {
	
	if (contentType == null) {
		throw ('error: contentType can not be null');
	}
	if (blobData == null) {
		throw ('error: blob can not be null');
	}
	if (databaseName == null) {
		throw ('error: dbName can not be null');
	}
	let args = {
		dbName: databaseName,				
		contentType: contentType,
		blobData: blobData,
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'saveBlob', [args]);
};

/* saveBlobFromEmbeddedResource - Saves Blob object 
 * from a file that is embedded in the Native project (AssetCatalog for iOS and
 * Resource folder for Android).  NOTE: successCallback returns metadata that
 * must be used  to retrieve the blob using the getBlob function.  It's very
 * important that you keep a reference to the metadata if you need to retrieve
 * the blob.  This is a helper function for javascript developers with no
 * Native equivalent call. 
 *
 * @param databaseName 
 *          {string} name of the database to retrieve document from 
 * @param contentType 
 *          {string} type of blob data you are storing 
 * @param resourceName 
 *          {string} string of native resource to use for blob data 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.saveBlobFromEmbeddedResource = function (databaseName, contentType, resourceName, successCallback, errorCallback){
	if (databaseName == null) {
		throw ('error: dbName can not be null');
	}
	if (contentType == null) {
		throw ('error: contentType can not be null');
	}
	if (resourceName == null) {
		throw ('error: key/blob can not be null');
	}
	let args = {
		dbName: databaseName,
		contentType: contentType,
		resourceName: resourceName,
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'saveBlobFromEmbeddedResource', [args]);
};

/* saveBlobFromFileUrl - Saves Blob object from 
 * a file that is saved on the device in a folder accessible by the application.
 * This might require application configuration changes to allow application to
 * read files stored on the device.
 * 
 * NOTE: successCallback returns metadata that must be used to retrieve the
 * blob using the getBlob function.  It's very important that you keep a
 * reference to the metadata if you need to retrieve the blob.  This is a
 * helper function for javascript developers with no Native equivalent call. 
 *
 * @param databaseName 
 *          {string} name of the database to update document
 * @param contentType 
 *          {string} type of blob data you are storing 
 * @param fileUrl 
 *          {string} string value of the path to the file to use for blob data 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.saveBlobFromFileUrl = function (databaseName, contentType, fileUrl, successCallback, errorCallback){
	if (databaseName == null) {
		throw ('error: dbName can not be null');
	}	

	if (contentType == null) {
		throw ('error: contentType can not be null');
	}
	if (fileUrl == null) {
		throw ('error: fileUrl can not be null');
	}
	let args = {
		dbName: databaseName,
		contentType: contentType,
		fileUrl: fileUrl,
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'saveBlobFromFileURL', [args]);
};

/* saveDocument - Saves a document to the database. When write operations are 
 * executed concurrently, the last writer will overwrite all other written 
 * values.  
 *
 * @param id 
 *          {string} id/key of the document to retrieve from the database 
 * @param document 
 *          {string} string of JSON to save/update in the database
 * @param dbName 
 *          {string} name of the database to retrieve document from 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.saveDocument = function (id, document, dbName, successCallback, errorCallback) {

	try {
		let args = this.getDocumentFullInfo(id, document, dbName);
		exec(successCallback, errorCallback, PLUGIN_NAME, 'saveDocument', [args]);
	}
	catch (e) {
		throw e;
	}
};

/* The fromSessionAuthentication is an authenticator that will authenticate
 * by using the session ID of the session created by a Sync Gateway 
 *
 * @param sessionId
 *          {string} Session ID of the session created by a Sync Gateway 
 * @param cookieName
 *          {string} cookie name that the session ID value will be set to when 
 *          communicating the * Sync Gateway. 
*/
cblite.prototype.SessionAuthenticator = function(cookieName, sessionId){
	var sa = new Object();
	sa.cookieName = cookieName;
	sa.sessionId = sessionId;
	sa.authType = "Session";
	return sa;
};



var cblitePlugin = new cblite();
Object.freeze(cblite.ReplicatorType);
Object.freeze(cblite.Domain);
Object.freeze(cblite.LogLevel);

module.exports = cblitePlugin;