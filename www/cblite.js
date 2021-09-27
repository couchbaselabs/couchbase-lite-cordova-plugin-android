var exec = require('cordova/exec');

/*
var ReplicatorConfiguration = require('./ReplicatorConfiguration')
*/

var PLUGIN_NAME = 'CBLite';

/*
 *  cblite - objects provide main API for Couchbase Lite integration 
*/
var cblite = function () { }

/* createOrOpenDatabase - Initializes a Couchbase Lite database with a given
 * name and database options. If the database does not yet exist, it will be
 * created. 
 * 
 * @param config 
 *          {DatabaseConfiguration} JSON object with dbName, directory, and encryptionKey
 *  		properties. 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.createOrOpenDatabase = function (config, successCallback, errorCallback) {
	exec(successCallback, errorCallback, PLUGIN_NAME, 'createOrOpenDatabase', [config]);
};

/* checkDatabase - Checks if database exists in the given directory path
 *
 * @param config
 *          {DatabaseConfiguration} JSON object with directory property.
 * @param successCallback
 *          {callback} javascript function to call if native code is successful
 * @param errorCallback
 *          {callback} javascript function to call if error happens in native
 *  		code
*/

cblite.prototype.databaseExists = function (config, successCallback, errorCallback) {	
	exec(successCallback, errorCallback, PLUGIN_NAME, 'databaseExists', [config]);
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

/* copyDatabase - Copies a canned databaes from the given path to a new 
 * database with the given name and the configuration. The new database will be
 * created at the directory specified in the configuration. Without given the
 * database configuration, the default configuration that is equivalent to
 * setting all properties in the configuration to nil will be used. 
 * 
 * @param fromPath 
 *          {string} the source database path of the prebuilt database.
 * @param newConfig
 *          {DatabaseConfiguration} JSON object with new database
 *  		directory and encryptionKey properties.
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.copyDatabase = function (fromPath, newConfig, successCallback, errorCallback) {
	let defaultConfig = {
		fromPath: fromPath,
		newConfig: newConfig
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'copyDatabase', [defaultConfig]);
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
}

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
}

/* getDocument - Gets a Document object with the given ID. 
 *
 * @param id 
 *          {string} id/key of the document to retrieve from the database 
 * @param dbName 
 *          {string} name of the database to retreive document from 
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

/* saveDocument - Saves a document to the database. When write operations are 
 * executed concurrently, the last writer will overwrite all other written 
 * values.  
 *
 * @param id 
 *          {string} id/key of the document to retrieve from the database 
 * @param document 
 *          {string} string of JSON to save/update in the database
 * @param dbName 
 *          {string} name of the database to retreive document from 
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

/* deleteDocument -  delete a document in the database. When write operations are 
 * executed concurrently, the last writer will overwrite all other written values.  
 *
 * @param id 
 *          {string} id/key of the document to remove from the database 
 * @param dbName 
 *          {string} name of the database to retreive document from 
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

/* getBlob - get a Blob object for the given metadata. 
 *
 * @param dbName 
 *          {string} name of the database to retreive document from 
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
		throw "error: dbName must be passed in to retreive blob";
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

/* saveBlob - Saves blob on the database.  NOTE: 
 * successCallback returns metadata that must be used to retreive the blob
 * using the getBlob function.  It's very important that you keep a reference
 * to the metadata if you need to retreive the blob. 
 *
 * @param databaseName 
 *          {string} name of the database to retreive document from 
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
 * must be used  to retreive the blob using the getBlob function.  It's very
 * important that you keep a reference to the metadata if you need to retreive
 * the blob.  This is a helper function for javascript developers with no
 * Native equivilant call. 
 *
 * @param databaseName 
 *          {string} name of the database to retreive document from 
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
 * NOTE: successCallback returns metadata that must be used to retreive the
 * blob using the getBlob function.  It's very important that you keep a
 * reference to the metadata if you need to retreive the blob.  This is a
 * helper function for javascript developers with no Native equivilant call. 
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

/* enableLogging - Log allows to configure console and file logger or to set a 
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
cblite.prototype.enableLogging = function (domain, logLevel, successCallback, errorCallback) {
	if (domain == null || logLevel == null) {
		throw ('error: domain can not be null');
	}
	let args = {
		domain: domain,
		logLevel: logLevel
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'enableLogging', [args])
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
}

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
}

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
}

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

/* replicatorStart - Initializes a replicator with the given configuration.  The
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
cblite.prototype.replicatorStart = function(config, successCallback, errorCallback) {
	if (config == null) {
		throw ("error: config can't be null");
	}
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStart', [config]);
};

/* replicatorStop - Stops a running replicator. This method returns immediately; 
 * when the replicator actually stops, the replicator will change its status’s 
 * activity level to .stopped and the replicator change notification will be 
 * notified accordingly.
 *
 * @param databaseName 
 *          {string} name of the database 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.replicatorStop = function(databaseName, successCallback, errorCallback) {
	if (databaseName == null) {
		throw ("error: database name can't be null");
	}
	let args = {
		dbName: databaseName	
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStop', [args]);
};

/* replicationAddListener - Adds a replication change listener. Changes will be 
 * posted on the main thread(android)/queue(ios).
 *
 * @param databaseName 
 *          {string} name of the database 
 * @param callbackName 
 *          {callback} javascript function to call when changes are posted.  
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.replicationAddListener = function(databaseName, callbackName, successCallback, errorCallback) {
	if (databaseName == null || callbackName == null) {
		throw ("error: database name and callbackName can't be null");
	}
	let args = {
		dbName: databaseName,	
		jsCallback: callbackName
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicationAddChangeListener', [args]);
}

/* replicationRemoveListener - removes the replication change listener. 
 *
 * @param databaseName 
 *          {string} name of the database 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.replicationRemoveListener = function(databaseName, successCallback, errorCallback) {
	if (databaseName == null) {
		throw ("error: database name can't be null");
	}
	let args = {
		dbName: databaseName	
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicationRemoveChangeListener', [args]);
};

/* getEmbeddedResourcePath - get's native file system path to embedded resource - helper method, no 
 * cblite mapping to native api, more convience to Cordova developers 
 *
 * @param resourceName 
 *          {string} name of the embeded resource where the database is stored 
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
}

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

/* DatabaseConfiguration - helper method to get database info for arguments 
 *
 * @param dbName 
 *          {string} name of the database 
 * @param config 
 *          {object} object with directory and encryptionKey properties 
*/
cblite.prototype.DatabaseConfiguration = function (dbName, config) {

	var obj = new Object();
	obj.dbName = dbName;
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


/** 
 *  Replicator configuration. 
 * 
 * @param databaseName
 *      {string} The local database name to replicate with the 
 * 		replication target. 
 * @param targetUrl
 *      {string} url of the eplication target to replicate with. 
*/
cblite.prototype.ReplicatorConfiguration = function (databaseName, targetUrl){
	var obj = new Object();

    obj.databaseName = databaseName;
    obj.target = targetUrl;

	obj.heartbeat = 0;
	obj.replicatorType = null; 
    obj.continuous = false; 
    obj.authenticator = null; 
    obj.acceptOnlySelfSignedServerCertificate = false; 
    obj.pinnedServerCertificate = ""; 
    obj.headers = [];
    obj.channels = [];
    obj.documentIds = []; 
    obj.allowReplicatingInBackground = false; 

	return obj;
};

/* The fromBasicAuthentiation is an authenticator that will authenticate using
 * HTTP Basic auth with the given username and password. This should only be 
 * used over an SSL/TLS connection, as otherwise it's very easy for anyone
 * sniffing network traffic to read the password.  
 * 
 * @param username
 *          {string} user to authenticate with - used with BasicAuthentication 
 * @param password
 *          {string} password to autheticate with - used with BasicAuthentiation 
*/
cblite.prototype.BasicAuthenticator = function(username, password) {
    var ba = new Object();
    ba.username = username;
    ba.password = password;
    ba.authType = "Basic";
    return ba;
};

/* The fromSessionAuthentication is an authenticator that will authenticate
 * by using the session ID of the session created by a Sync Gateway 
 *
 * @param sessionId
 *          {string} Session ID of the session created by a Sync Gateway 
 * @param cookieName
 *          {string} cookie name that the session ID value will be set to when 
 *          communicating the * Sync Gateaway. 
*/
cblite.prototype.SessionAuthenticator = function(cookieName, sessionId){
	var sa = new Object();
	sa.cookieName = cookieName;
	sa.sessionId = sessionId;
	sa.authType = "Session";
	return sa;
};

cblite.prototype.ReplicatorType = { 
	"PUSH_AND_PULL": "PUSHANDPULL", 
	"PUSH": "PUSH", 
	"PULL": "PULL"
};

var cblitePlugin = new cblite();
Object.freeze(cblite.ReplicatorType);

module.exports = cblitePlugin;