

package com.yourapp.model;


import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.Map;


// -----------------------------
// Organization Entity
// -----------------------------
public class Organisation {
private UUID id;
private String name;
private String slug;
private String description;
private String country;
private ZonedDateTime createdAt;
private ZonedDateTime updatedAt;


// Getters & Setters
public UUID getId() { return id; }
public void setId(UUID id) { this.id = id; }


public String getName() { return name; }
public void setName(String name) { this.name = name; }


public String getSlug() { return slug; }
public void setSlug(String slug) { this.slug = slug; }


public String getDescription() { return description; }
public void setDescription(String description) { this.description = description; }


public String getCountry() { return country; }
public void setCountry(String country) { this.country = country; }


public ZonedDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }


public ZonedDateTime getUpdatedAt() { return updatedAt; }
public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}