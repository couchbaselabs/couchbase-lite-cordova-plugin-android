package com.couchbase.cblite.objects;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReplicatorConfigHash {



  private String databaseName;
  private boolean continuous;
  private int replicationType;
  private List<String> channels;
  private List<String> documentIds;

  public ReplicatorConfigHash() {

  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public boolean isContinuous() {
    return continuous;
  }

  public void setContinuous(boolean continuous) {
    this.continuous = continuous;
  }

  public int getReplicationType() {
    return replicationType;
  }

  public void setReplicationType(int replicationType) {
    this.replicationType = replicationType;
  }

  public List<String> getChannels() {
    return channels;
  }

  public void setChannels(List<String> channels) {
    this.channels = channels;
  }

  public List<String> getDocumentIds() {
    return documentIds;
  }

  public void setDocumentIds(List<String> documentIds) {
    this.documentIds = documentIds;
  }

  @Override
  public int hashCode()
  {
    return Math.abs(Objects.hash(this.databaseName, this.continuous, this.replicationType, this.channels, this.documentIds));
  }
}
