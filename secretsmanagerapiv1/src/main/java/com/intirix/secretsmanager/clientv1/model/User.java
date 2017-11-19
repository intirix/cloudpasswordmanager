/*
 * Secrets Manager
 * API to access the Secrets Manager
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.intirix.secretsmanager.clientv1.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * User
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-11-19T01:54:47.421Z")
public class User {
  @SerializedName("username")
  private String username = null;

  @SerializedName("id")
  private String id = null;

  /**
   * Is the user an admin
   */
  @JsonAdapter(AdminEnum.Adapter.class)
  public enum AdminEnum {
    Y("Y"),
    
    N("N");

    private String value;

    AdminEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static AdminEnum fromValue(String text) {
      for (AdminEnum b : AdminEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<AdminEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final AdminEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public AdminEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return AdminEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("admin")
  private AdminEnum admin = null;

  /**
   * Is the user enabled
   */
  @JsonAdapter(EnabledEnum.Adapter.class)
  public enum EnabledEnum {
    Y("Y"),
    
    N("N");

    private String value;

    EnabledEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static EnabledEnum fromValue(String text) {
      for (EnabledEnum b : EnabledEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<EnabledEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final EnabledEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public EnabledEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return EnabledEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("enabled")
  private EnabledEnum enabled = null;

  /**
   * Gets or Sets passwordAuth
   */
  @JsonAdapter(PasswordAuthEnum.Adapter.class)
  public enum PasswordAuthEnum {
    Y("Y"),
    
    N("N");

    private String value;

    PasswordAuthEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static PasswordAuthEnum fromValue(String text) {
      for (PasswordAuthEnum b : PasswordAuthEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<PasswordAuthEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final PasswordAuthEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public PasswordAuthEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return PasswordAuthEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("passwordAuth")
  private PasswordAuthEnum passwordAuth = null;

  @SerializedName("displayName")
  private String displayName = null;

  public User username(String username) {
    this.username = username;
    return this;
  }

   /**
   * Username
   * @return username
  **/
  @ApiModelProperty(value = "Username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public User id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public User admin(AdminEnum admin) {
    this.admin = admin;
    return this;
  }

   /**
   * Is the user an admin
   * @return admin
  **/
  @ApiModelProperty(value = "Is the user an admin")
  public AdminEnum getAdmin() {
    return admin;
  }

  public void setAdmin(AdminEnum admin) {
    this.admin = admin;
  }

  public User enabled(EnabledEnum enabled) {
    this.enabled = enabled;
    return this;
  }

   /**
   * Is the user enabled
   * @return enabled
  **/
  @ApiModelProperty(value = "Is the user enabled")
  public EnabledEnum getEnabled() {
    return enabled;
  }

  public void setEnabled(EnabledEnum enabled) {
    this.enabled = enabled;
  }

  public User passwordAuth(PasswordAuthEnum passwordAuth) {
    this.passwordAuth = passwordAuth;
    return this;
  }

   /**
   * Get passwordAuth
   * @return passwordAuth
  **/
  @ApiModelProperty(value = "")
  public PasswordAuthEnum getPasswordAuth() {
    return passwordAuth;
  }

  public void setPasswordAuth(PasswordAuthEnum passwordAuth) {
    this.passwordAuth = passwordAuth;
  }

  public User displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

   /**
   * What get&#39;s displayed as the user&#39;s name
   * @return displayName
  **/
  @ApiModelProperty(value = "What get's displayed as the user's name")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.username, user.username) &&
        Objects.equals(this.id, user.id) &&
        Objects.equals(this.admin, user.admin) &&
        Objects.equals(this.enabled, user.enabled) &&
        Objects.equals(this.passwordAuth, user.passwordAuth) &&
        Objects.equals(this.displayName, user.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, id, admin, enabled, passwordAuth, displayName);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    admin: ").append(toIndentedString(admin)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    passwordAuth: ").append(toIndentedString(passwordAuth)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

