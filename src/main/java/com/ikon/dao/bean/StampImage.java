package com.ikon.dao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class StampImage
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final int LAYER_UNDER_CONTENT = 0;
  public static final int LAYER_OVER_CONTENT = 1;
  private long id;
  private String name;
  private String description;
  private String imageContent;
  private String imageMime;
  private int layer = 1;
  private float opacity = 0.5F;
  private String exprX = "PAGE_CENTER - IMAGE_WIDTH / 2";
  private String exprY = "PAGE_MIDDLE - IMAGE_HEIGHT / 2";
  private boolean active;
  private Set<String> users = new HashSet<String>();

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    if ((description != null) && (description.length() > 512))
      this.description = description.substring(0, 512);
    else
      this.description = description;
  }

  public String getImageContent()
  {
    return this.imageContent;
  }

  public void setImageContent(String imageContent) {
    this.imageContent = imageContent;
  }

  public String getImageMime() {
    return this.imageMime;
  }

  public void setImageMime(String imageMime) {
    this.imageMime = imageMime;
  }

  public int getLayer() {
    return this.layer;
  }

  public void setLayer(int layer) {
    this.layer = layer;
  }

  public float getOpacity() {
    return this.opacity;
  }

  public void setOpacity(float opacity) {
    this.opacity = opacity;
  }

  public String getExprX() {
    return this.exprX;
  }

  public void setExprX(String exprX) {
    this.exprX = exprX;
  }

  public String getExprY() {
    return this.exprY;
  }

  public void setExprY(String exprY) {
    this.exprY = exprY;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Set<String> getUsers() {
    return this.users;
  }

  public void setUsers(Set<String> users) {
    this.users = users;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("id="); sb.append(this.id);
    sb.append(", name="); sb.append(this.name);
    sb.append(", description="); sb.append(this.description);
    sb.append(", imageMime="); sb.append(this.imageMime);
    sb.append(", imageContent="); sb.append("[BIG]");
    sb.append(", layer="); sb.append(this.layer);
    sb.append(", opacity="); sb.append(this.opacity);
    sb.append(", exprX="); sb.append(this.exprX);
    sb.append(", exprY="); sb.append(this.exprY);
    sb.append(", active="); sb.append(this.active);
    sb.append(", users="); sb.append(this.users);
    sb.append("}");
    return sb.toString();
  }
}