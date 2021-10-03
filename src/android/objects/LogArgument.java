package com.couchbase.cblite.objects;

import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogLevel;

public class LogArgument {

  private LogLevel logLevel;
  private LogDomain domain;

  public LogLevel getLogLevel() {
    return logLevel;
  }

  public LogDomain getDomain() {
    return domain;
  }

  public LogArgument(String logLevel, String domain) {

    switch (domain) {
      case "database":
        this.domain = LogDomain.DATABASE;
      case "query":
        this.domain = LogDomain.QUERY;
      case "replicator":
        this.domain = LogDomain.REPLICATOR;
      case "network":
        this.domain = LogDomain.NETWORK;
      case "listener":
        this.domain = LogDomain.LISTENER;
      default:
    }

    switch (logLevel) {
      case "debug":
        this.logLevel = LogLevel.DEBUG;
      case "verbose":
        this.logLevel = LogLevel.VERBOSE;
      case "info":
        this.logLevel = LogLevel.INFO;
      case "warning":
        this.logLevel = LogLevel.WARNING;
      case "error":
        this.logLevel = LogLevel.ERROR;
      default:
        this.logLevel = LogLevel.NONE;
    }
  }

}
