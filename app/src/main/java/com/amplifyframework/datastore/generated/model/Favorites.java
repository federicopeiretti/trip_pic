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

/** This is an auto generated class representing the Favorites type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Favorites", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Favorites implements Model {
  public static final QueryField ID = field("Favorites", "id");
  public static final QueryField POST = field("Favorites", "post");
  public static final QueryField FAVORITES_USER_ID = field("Favorites", "favoritesUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User", isRequired = true) @HasOne(associatedWith = "id", type = User.class) User user = null;
  private final @ModelField(targetType="String") List<String> post;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="ID", isRequired = true) String favoritesUserId;
  public String getId() {
      return id;
  }
  
  public User getUser() {
      return user;
  }
  
  public List<String> getPost() {
      return post;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getFavoritesUserId() {
      return favoritesUserId;
  }
  
  private Favorites(String id, List<String> post, String favoritesUserId) {
    this.id = id;
    this.post = post;
    this.favoritesUserId = favoritesUserId;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Favorites favorites = (Favorites) obj;
      return ObjectsCompat.equals(getId(), favorites.getId()) &&
              ObjectsCompat.equals(getPost(), favorites.getPost()) &&
              ObjectsCompat.equals(getCreatedAt(), favorites.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), favorites.getUpdatedAt()) &&
              ObjectsCompat.equals(getFavoritesUserId(), favorites.getFavoritesUserId());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getPost())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getFavoritesUserId())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Favorites {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("post=" + String.valueOf(getPost()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("favoritesUserId=" + String.valueOf(getFavoritesUserId()))
      .append("}")
      .toString();
  }
  
  public static FavoritesUserIdStep builder() {
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
  public static Favorites justId(String id) {
    return new Favorites(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      post,
      favoritesUserId);
  }
  public interface FavoritesUserIdStep {
    BuildStep favoritesUserId(String favoritesUserId);
  }
  

  public interface BuildStep {
    Favorites build();
    BuildStep id(String id);
    BuildStep post(List<String> post);
  }
  

  public static class Builder implements FavoritesUserIdStep, BuildStep {
    private String id;
    private String favoritesUserId;
    private List<String> post;
    @Override
     public Favorites build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Favorites(
          id,
          post,
          favoritesUserId);
    }
    
    @Override
     public BuildStep favoritesUserId(String favoritesUserId) {
        Objects.requireNonNull(favoritesUserId);
        this.favoritesUserId = favoritesUserId;
        return this;
    }
    
    @Override
     public BuildStep post(List<String> post) {
        this.post = post;
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
    private CopyOfBuilder(String id, List<String> post, String favoritesUserId) {
      super.id(id);
      super.favoritesUserId(favoritesUserId)
        .post(post);
    }
    
    @Override
     public CopyOfBuilder favoritesUserId(String favoritesUserId) {
      return (CopyOfBuilder) super.favoritesUserId(favoritesUserId);
    }
    
    @Override
     public CopyOfBuilder post(List<String> post) {
      return (CopyOfBuilder) super.post(post);
    }
  }
  
}
