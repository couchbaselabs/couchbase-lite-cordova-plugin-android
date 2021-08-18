var exec = require('cordova/exec');

/*
var Authenticator = require('./Authenticator');
var ReplicatorConfiguration = require('./ReplicatorConfiguration')
var ReplicationType = require('./ReplicatorType')
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
 * @param dbName 
 *          {string} name of the database to open or create 
 * @param config 
 *          {databaseConfig} JSON object with directory and encryptionKey
 *  		properties. 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.createOrOpenDatabase = function (dbName, config, successCallback, errorCallback) {
	let defaultConfig = this.getConfig(dbName, config);
	this.dbName = dbName;
	exec(successCallback, errorCallback, PLUGIN_NAME, 'createOrOpenDatabase', [defaultConfig]);
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
 * @param currentDbName 
 *          {string} name of the database to copy 
 * @param currentConfig
 *          {databaseConfig} JSON object with current database directory and
 *  		encryptionKey properties. 
 * @param newDbName 
 *          {string} name of the new database that will be created from the
 *  		copy 
 * @param newConfig
 *          {databaseConfig} JSON object with new database directory and
 *  		encryptionKey properties.
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.copyDatabase = function (currentDbName, currentConfig, newDbName, newConfig, successCallback, errorCallback) {
	let currentDbConfig = this.getConfig(currentDbName, currentConfig);
	let newDbConfig = this.getConfig(newDbName, newConfig);
	let defaultConfig = {
		currentConfig: currentDbConfig,
		newConfig: newDbConfig
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
cblite.prototype.deleteDatabase = function(dbName, config,  successCallback, errorCallback) {
	let defaultConfig = this.getConfig(dbName, config);
	exec(successCallback, errorCallback, PLUGIN_NAME, 'deleteDatabase', [defaultConfig]);
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

/* mutableDocumentSetBlob - Set a Blob object for the given key.  NOTE: 
 * successCallback returns metadata that must be used to retreive the blob
 * using the getBlob function.  It's very important that you keep a reference
 * to the metadata if you need to retreive the blob. 
 *
 * @param documentId 
 *           {string} id/key of the document to update 
 * @param databaseName 
 *          {string} name of the database to retreive document from 
 * @param key 
 *          {string} string of key (property) to associate blob metadata with 
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
cblite.prototype.mutableDocumentSetBlob = function (documentId, databaseName, key, contentType, blobData, successCallback, errorCallback) {
	if (documentId == null) {
		throw ('error: documentId can not be null');
	}	
	if (contentType == null) {
		throw ('error: contentType can not be null');
	}
	if (key == null || blobData == null) {
		throw ('error: key/blob can not be null');
	}
	if (databaseName == null) {
		throw ('error: dbName can not be null');
	}
	let args = {
		dbName: databaseName,
		id: documentId,
		key: key,
		contentType: contentType,
		blobData: blobData,
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'mutableDocumentSetBlob', [args]);
};

/* documentSetBlobFromEmbeddedResource - Set a Blob object for the given key
 * from a file that is embedded in the Native project (AssetCatalog for iOS and
 * Resource folder for Android).  NOTE: successCallback returns metadata that
 * must be used  to retreive the blob using the getBlob function.  It's very
 * important that you keep a reference to the metadata if you need to retreive
 * the blob.  This is a helper function for javascript developers with no
 * Native equivilant call. 
 *
 * @param documentId 
 *           {string} id/key of the document to update 
 * @param databaseName 
 *          {string} name of the database to retreive document from 
 * @param contentType 
 *          {string} type of blob data you are storing 
 * @param key 
 *          {string} string of key (property) to associate blob metadata with 
 * @param resourceName 
 *          {string} string of native resource to use for blob data 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.documentSetBlobFromEmbeddedResource = function (documentId, databaseName, contentType, key, resourceName, successCallback, errorCallback){
	if (databaseName == null) {
		throw ('error: dbName can not be null');
	}
	if (documentId == null) {
		throw('error: documentId can not be null');
	}	
	if (contentType == null) {
		throw ('error: contentType can not be null');
	}
	if (key == null || resourceName == null) {
		throw ('error: key/blob can not be null');
	}
	let args = {
		dbName: databaseName,
		id: documentId,
		key: key,
		contentType: contentType,
		resourceName: resourceName,
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'mutableDocumentSetBlobFromEmbeddedResource', [args]);
};

/* documentSetBlobFromFileUrl - Set a Blob object for the given key from 
 * a file that is saved on the device in a folder accessible by the application.
 * This might require application configuration changes to allow application to
 * read files stored on the device.
 * 
 * NOTE: successCallback returns metadata that must be used to retreive the
 * blob using the getBlob function.  It's very important that you keep a
 * reference to the metadata if you need to retreive the blob.  This is a
 * helper function for javascript developers with no Native equivilant call. 
 *
 * @param documentId 
 *           {string} id/key of the document to update 
 * @param databaseName 
 *          {string} name of the database to update document
 * @param contentType 
 *          {string} type of blob data you are storing 
 * @param key 
 *          {string} string of key (property) to associate blob metadata with 
 * @param fileUrl 
 *          {string} string value of the path to the file to use for blob data 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.documentSetBlobFromFileUrl = function (documentId, databaseName, contentType, key, fileUrl, successCallback, errorCallback){
	if (databaseName == null) {
		throw ('error: dbName can not be null');
	}	
	if (documentId == null) {
		throw ('error: documentId can not be null');
	}	
	if (contentType == null) {
		throw ('error: contentType can not be null');
	}
	if (key == null || fileUrl == null) {
		throw ('error: key/fileUrl can not be null');
	}
	let args = {
		dbName: databaseName,
		id: documentId,
		key: key,
		contentType: contentType,
		fileUrl: fileUrl,
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'mutableDocumentSetBlobFromFileUrl', [args]);
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
	if (domain == null || logLevel) {
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
 * @param callbackName 
 *          {callback} javascript function to call when changes are posted.  
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native
 *  		code 
*/
cblite.prototype.queryAddListener = function(databaseName, callbackName, successCallback, errorCallback) {
	if (databaseName == null || callbackName == null) {
		throw ("error: database name and callbackName can't be null");
	}
	let args = {
		dbName: databaseName,	
		jsCallback: callbackName
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'queryAddChangeListener', [args]);
}

/* queryRemoveListener - Removes a query change listener. 
 *
 * @param databaseName 
 *          {string} name of the database to remove listener from
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native 
 * 			code 
*/
cblite.prototype.queryRemoveListener = function(databaseName, successCallback, errorCallback) {
	if (databaseName == null) {
		throw ("error: database name can't be null");
	}
	let args = {
		dbName: databaseName	
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
 * @param databaseName 
 *          {string} name of the database 
 * @param config 
 *          {ReplicatorConfiguration} The configuration 
 * @param successCallback 
 *          {callback} javascript function to call if native code is successful 
 * @param errorCallback 
 *          {callback} javascript function to call if error happens in native code 
*/
cblite.prototype.replicatorStart = function(databaseName, config, successCallback, errorCallback) {
	if (databaseName == null || config == null) {
		throw ("error: database name or config can't be null");
	}
	let args = {
		dbName: databaseName,
		config: config
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'replicatorStart', [args]);
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

/* getConfig - helper method to get database info for arguments 
 *
 * @param dbName 
 *          {string} name of the database 
 * @param config 
 *          {object} object with directory and encryptionKey properties 
*/
cblite.prototype.getConfig = function (dbName, config) {

	let defaultConfig = {
		dbName: dbName,
		directory: "",
		encryptionKey: ""
	};
	//if config is passed in add values
	if (config != null) {
		if (config.hasOwnProperty('encryptionKey')) {
			defaultConfig.encryptionKey = config.encryptionKey;
		}
		if (config.hasOwnProperty('directory')) {
			defaultConfig.directory = config.directory;
		}
	}
	return defaultConfig;

	cblite.dbName = "";
};

var cblitePlugin = new cblite();
module.exports = cblitePlugin;