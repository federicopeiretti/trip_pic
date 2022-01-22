package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasOne;
import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Friend type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Friends", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Friend implements Model {
  public static final QueryField ID = field("Friend", "id");
  public static final QueryField FRIENDS = field("Friend", "friends");
  public static final QueryField FRIEND_USER_ID = field("Friend", "friendUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User", isRequired = true) @HasOne(associatedWith = "id", type = User.class) User user = null;
  private final @ModelField(targetType="String") List<String> friends;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID", isRequired = true) String friendUserId;
  public String getId() {
      return id;
  }
  
  public User getUser() {
      return user;
  }
  
  public List<String> getFriends() {
      return friends;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getFriendUserId() {
      return friendUserId;
  }
  
  private Friend(String id, List<String> friends, String friendUserId) {
    this.id = id;
    this.friends = friends;
    this.friendUserId = friendUserId;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Friend friend = (Friend) obj;
      return ObjectsCompat.equals(getId(), friend.getId()) &&
              ObjectsCompat.equals(getFriends(), friend.getFriends()) &&
              ObjectsCompat.equals(getCreatedAt(), friend.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), friend.getUpdatedAt()) &&
              ObjectsCompat.equals(getFriendUserId(), friend.getFriendUserId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getFriends())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getFriendUserId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Friend {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("friends=" + String.valueOf(getFriends()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("friendUserId=" + String.valueOf(getFriendUserId()))
      .append("}")
      .toString();
  }
  
  public static FriendUserIdStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Friend justId(String id) {
    return new Friend(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      friends,
      friendUserId);
  }
  public interface FriendUserIdStep {
    BuildStep friendUserId(String friendUserId);
  }
  

  public interface BuildStep {
    Friend build();
    BuildStep id(String id);
    BuildStep friends(List<String> friends);
  }
  

  public static class Builder implements FriendUserIdStep, BuildStep {
    private String id;
    private String friendUserId;
    private List<String> friends;
    @Override
     public Friend build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Friend(
          id,
          friends,
          friendUserId);
    }
    
    @Override
     public BuildStep friendUserId(String friendUserId) {
        Objects.requireNonNull(friendUserId);
        this.friendUserId = friendUserId;
        return this;
    }
    
    @Override
     public BuildStep friends(List<String> friends) {
        this.friends = friends;
        return this;
    }
    
    /** 
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, List<String> friends, String friendUserId) {
      super.id(id);
      super.friendUserId(friendUserId)
        .friends(friends);
    }
    
    @Override
     public CopyOfBuilder friendUserId(String friendUserId) {
      return (CopyOfBuilder) super.friendUserId(friendUserId);
    }
    
    @Override
     public CopyOfBuilder friends(List<String> friends) {
      return (CopyOfBuilder) super.friends(friends);
    }
  }
  
}
