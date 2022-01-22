package com.amplifyframework.datastore.generated.model;

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

/** This is an auto generated class representing the Location type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Locations", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Location implements Model {
  public static final QueryField ID = field("Location", "id");
  public static final QueryField LAT = field("Location", "lat");
  public static final QueryField LNG = field("Location", "lng");
  public static final QueryField NAME = field("Location", "name");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Float", isRequired = true) Double lat;
  private final @ModelField(targetType="Float", isRequired = true) Double lng;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public Double getLat() {
      return lat;
  }
  
  public Double getLng() {
      return lng;
  }
  
  public String getName() {
      return name;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Location(String id, Double lat, Double lng, String name) {
    this.id = id;
    this.lat = lat;
    this.lng = lng;
    this.name = name;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Location location = (Location) obj;
      return ObjectsCompat.equals(getId(), location.getId()) &&
              ObjectsCompat.equals(getLat(), location.getLat()) &&
              ObjectsCompat.equals(getLng(), location.getLng()) &&
              ObjectsCompat.equals(getName(), location.getName()) &&
              ObjectsCompat.equals(getCreatedAt(), location.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), location.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getLat())
      .append(getLng())
      .append(getName())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Location {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("lat=" + String.valueOf(getLat()) + ", ")
      .append("lng=" + String.valueOf(getLng()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static LatStep builder() {
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
  public static Location justId(String id) {
    return new Location(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      lat,
      lng,
      name);
  }
  public interface LatStep {
    LngStep lat(Double lat);
  }
  

  public interface LngStep {
    NameStep lng(Double lng);
  }
  

  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    Location build();
    BuildStep id(String id);
  }
  

  public static class Builder implements LatStep, LngStep, NameStep, BuildStep {
    private String id;
    private Double lat;
    private Double lng;
    private String name;
    @Override
     public Location build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Location(
          id,
          lat,
          lng,
          name);
    }
    
    @Override
     public LngStep lat(Double lat) {
        Objects.requireNonNull(lat);
        this.lat = lat;
        return this;
    }
    
    @Override
     public NameStep lng(Double lng) {
        Objects.requireNonNull(lng);
        this.lng = lng;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
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
    private CopyOfBuilder(String id, Double lat, Double lng, String name) {
      super.id(id);
      super.lat(lat)
        .lng(lng)
        .name(name);
    }
    
    @Override
     public CopyOfBuilder lat(Double lat) {
      return (CopyOfBuilder) super.lat(lat);
    }
    
    @Override
     public CopyOfBuilder lng(Double lng) {
      return (CopyOfBuilder) super.lng(lng);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
  }
  
}
