var exec = require('cordova/exec');
var PLUGIN_NAME = 'CBLite';

var cblite = function () { }

cblite.prototype.createDatabase = function (dbName, config, successCallback, errorCallback) {
	let defaultConfig = this.getConfig(dbName, config);
	exec(successCallback, errorCallback, PLUGIN_NAME, 'createDatabase', [defaultConfig]);
};

cblite.prototype.closeDatabase = function (dbName, successCallback, errorCallback) {
	exec(successCallback, errorCallback, PLUGIN_NAME, 'closeDatabase', [dbName]);
};

cblite.prototype.copyDatabase = function (currentDbName, currentConfig, newDbName, newConfig, successCallback, errorCallback) {
	let currentDbConfig = this.getConfig(currentDbName, currentConfig);
	let newDbConfig = this.getConfig(newDbName, newConfig);
	let defaultConfig = {
		currentConfig: currentDbConfig,
		newConfig: newDbConfig
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'copyDatabase', [defaultConfig]);
};

cblite.prototype.deleteDatabase = function(dbName, config,  successCallback, errorCallback) {
	let defaultConfig = this.getConfig(dbName, config);
	exec(successCallback, errorCallback, PLUGIN_NAME, 'deleteDatabase', [defaultConfig]);
};

//todo add the add/remove change listeners later
cblite.prototype.getDocument = function (id, dbName, successCallback, errorCallback) {
	try {
		let args = this.getDocumentInfo(id, dbName);
		exec(successCallback, errorCallback, PLUGIN_NAME, 'getDocument', [args]);
	} catch (e) {
		throw e;
	}
};

cblite.prototype.saveDocument = function (id, document, dbName, successCallback, errorCallback) {

	try {
		let args = this.getDocumentFullInfo(id, document, dbName);
		exec(successCallback, errorCallback, PLUGIN_NAME, 'saveDocument', [args]);
	}
	catch (e) {
		throw e;
	}
};

cblite.prototype.deleteDocument = function (id, dbName, successCallback, errorCallback) {

	try {
		let args = this.getDocumentInfo(id, dbName);
		exec(successCallback, errorCallback, PLUGIN_NAME, 'deleteDocument', [args]);
	}
	catch (e) {
		throw e;
	}
};

cblite.prototype.createMutableDocument = function (id, dbName, successCallback, errorCallback) {
	if (id == null) {
		throw ('error: id can not be null');
	}
	exec(successCallback, errorCallback, PLUGIN_NAME, 'createMutableDocument', [id]);
};

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

cblite.prototype.dbRemoveListener = function(databaseName, successCallback, errorCallback) {
	if (databaseName == null) {
		throw ("error: database name can't be null");
	}
	let args = {
		dbName: databaseName	
	};
	exec(successCallback, errorCallback, PLUGIN_NAME, 'removeChangeListener', [args]);
}

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
};

var cblitePlugin = new cblite();
module.exports = cblitePlugin;