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

/** This is an auto generated class representing the Post type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Posts", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Post implements Model {
  public static final QueryField ID = field("Post", "id");
  public static final QueryField OWNER_NAME = field("Post", "ownerName");
  public static final QueryField LOCATION_NAME = field("Post", "locationName");
  public static final QueryField TITLE = field("Post", "title");
  public static final QueryField DESCRIPTION = field("Post", "description");
  public static final QueryField IMAGE_KEY = field("Post", "imageKey");
  public static final QueryField IMAGE_URL = field("Post", "imageUrl");
  public static final QueryField POST_OWNER_ID = field("Post", "postOwnerId");
  public static final QueryField POST_LOCATION_ID = field("Post", "postLocationId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User", isRequired = true) @HasOne(associatedWith = "id", type = User.class) User owner = null;
  private final @ModelField(targetType="String") String ownerName;
  private final @ModelField(targetType="Location", isRequired = true) @HasOne(associatedWith = "id", type = Location.class) Location location = null;
  private final @ModelField(targetType="String") String locationName;
  private final @ModelField(targetType="String") String title;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="String") String imageKey;
  private final @ModelField(targetType="String") String imageUrl;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID", isRequired = true) String postOwnerId;
  private final @ModelField(targetType="ID", isRequired = true) String postLocationId;
  public String getId() {
      return id;
  }
  
  public User getOwner() {
      return owner;
  }
  
  public String getOwnerName() {
      return ownerName;
  }
  
  public Location getLocation() {
      return location;
  }
  
  public String getLocationName() {
      return locationName;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getDescription() {
      return description;
  }
  
  public String getImageKey() {
      return imageKey;
  }
  
  public String getImageUrl() {
      return imageUrl;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getPostOwnerId() {
      return postOwnerId;
  }
  
  public String getPostLocationId() {
      return postLocationId;
  }
  
  private Post(String id, String ownerName, String locationName, String title, String description, String imageKey, String imageUrl, String postOwnerId, String postLocationId) {
    this.id = id;
    this.ownerName = ownerName;
    this.locationName = locationName;
    this.title = title;
    this.description = description;
    this.imageKey = imageKey;
    this.imageUrl = imageUrl;
    this.postOwnerId = postOwnerId;
    this.postLocationId = postLocationId;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Post post = (Post) obj;
      return ObjectsCompat.equals(getId(), post.getId()) &&
              ObjectsCompat.equals(getOwnerName(), post.getOwnerName()) &&
              ObjectsCompat.equals(getLocationName(), post.getLocationName()) &&
              ObjectsCompat.equals(getTitle(), post.getTitle()) &&
              ObjectsCompat.equals(getDescription(), post.getDescription()) &&
              ObjectsCompat.equals(getImageKey(), post.getImageKey()) &&
              ObjectsCompat.equals(getImageUrl(), post.getImageUrl()) &&
              ObjectsCompat.equals(getCreatedAt(), post.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), post.getUpdatedAt()) &&
              ObjectsCompat.equals(getPostOwnerId(), post.getPostOwnerId()) &&
              ObjectsCompat.equals(getPostLocationId(), post.getPostLocationId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getOwnerName())
      .append(getLocationName())
      .append(getTitle())
      .append(getDescription())
      .append(getImageKey())
      .append(getImageUrl())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getPostOwnerId())
      .append(getPostLocationId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Post {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("ownerName=" + String.valueOf(getOwnerName()) + ", ")
      .append("locationName=" + String.valueOf(getLocationName()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("imageKey=" + String.valueOf(getImageKey()) + ", ")
      .append("imageUrl=" + String.valueOf(getImageUrl()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("postOwnerId=" + String.valueOf(getPostOwnerId()) + ", ")
      .append("postLocationId=" + String.valueOf(getPostLocationId()))
      .append("}")
      .toString();
  }
  
  public static PostOwnerIdStep builder() {
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
  public static Post justId(String id) {
    return new Post(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      ownerName,
      locationName,
      title,
      description,
      imageKey,
      imageUrl,
      postOwnerId,
      postLocationId);
  }
  public interface PostOwnerIdStep {
    PostLocationIdStep postOwnerId(String postOwnerId);
  }
  

  public interface PostLocationIdStep {
    BuildStep postLocationId(String postLocationId);
  }
  

  public interface BuildStep {
    Post build();
    BuildStep id(String id);
    BuildStep ownerName(String ownerName);
    BuildStep locationName(String locationName);
    BuildStep title(String title);
    BuildStep description(String description);
    BuildStep imageKey(String imageKey);
    BuildStep imageUrl(String imageUrl);
  }
  

  public static class Builder implements PostOwnerIdStep, PostLocationIdStep, BuildStep {
    private String id;
    private String postOwnerId;
    private String postLocationId;
    private String ownerName;
    private String locationName;
    private String title;
    private String description;
    private String imageKey;
    private String imageUrl;
    @Override
     public Post build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Post(
          id,
          ownerName,
          locationName,
          title,
          description,
          imageKey,
          imageUrl,
          postOwnerId,
          postLocationId);
    }
    
    @Override
     public PostLocationIdStep postOwnerId(String postOwnerId) {
        Objects.requireNonNull(postOwnerId);
        this.postOwnerId = postOwnerId;
        return this;
    }
    
    @Override
     public BuildStep postLocationId(String postLocationId) {
        Objects.requireNonNull(postLocationId);
        this.postLocationId = postLocationId;
        return this;
    }
    
    @Override
     public BuildStep ownerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }
    
    @Override
     public BuildStep locationName(String locationName) {
        this.locationName = locationName;
        return this;
    }
    
    @Override
     public BuildStep title(String title) {
        this.title = title;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep imageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }
    
    @Override
     public BuildStep imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
    private CopyOfBuilder(String id, String ownerName, String locationName, String title, String description, String imageKey, String imageUrl, String postOwnerId, String postLocationId) {
      super.id(id);
      super.postOwnerId(postOwnerId)
        .postLocationId(postLocationId)
        .ownerName(ownerName)
        .locationName(locationName)
        .title(title)
        .description(description)
        .imageKey(imageKey)
        .imageUrl(imageUrl);
    }
    
    @Override
     public CopyOfBuilder postOwnerId(String postOwnerId) {
      return (CopyOfBuilder) super.postOwnerId(postOwnerId);
    }
    
    @Override
     public CopyOfBuilder postLocationId(String postLocationId) {
      return (CopyOfBuilder) super.postLocationId(postLocationId);
    }
    
    @Override
     public CopyOfBuilder ownerName(String ownerName) {
      return (CopyOfBuilder) super.ownerName(ownerName);
    }
    
    @Override
     public CopyOfBuilder locationName(String locationName) {
      return (CopyOfBuilder) super.locationName(locationName);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder imageKey(String imageKey) {
      return (CopyOfBuilder) super.imageKey(imageKey);
    }
    
    @Override
     public CopyOfBuilder imageUrl(String imageUrl) {
      return (CopyOfBuilder) super.imageUrl(imageUrl);
    }
  }
  
}
