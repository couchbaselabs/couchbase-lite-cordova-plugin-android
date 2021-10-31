package com.couchbase.cblite.objects;

import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Replicator;

public class ReplicatorResource {

  //replication
  private Replicator replicator;
  private ListenerToken replicatorChangeListenerToken;

  public Replicator getReplicator() {
    return replicator;
  }

  public void setReplicator(Replicator replicator) {
    this.replicator = replicator;
  }

  public ListenerToken getReplicatorChangeListenerToken() {
    return replicatorChangeListenerToken;
  }

  public void setReplicatorChangeListenerToken(ListenerToken replicatorChangeListenerToken) {
    this.replicatorChangeListenerToken = replicatorChangeListenerToken;
  }

  public String getReplicatorChangeListenerJSFunction() {
    return replicatorChangeListenerJSFunction;
  }

  public void setReplicatorChangeListenerJSFunction(String replicatorChangeListenerJSFunction) {
    this.replicatorChangeListenerJSFunction = replicatorChangeListenerJSFunction;
  }

  private String replicatorChangeListenerJSFunction;

  public ReplicatorResource(Replicator replicator){
    this.replicator = replicator;
  }

}
