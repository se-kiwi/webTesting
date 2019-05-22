package com.kiwi.webtest.cookies;

/*
  {
    "domain": "www.wjx.cn",
    "expirationDate": 1590044191,
    "hostOnly": true,
    "httpOnly": false,
    "name": "_umdata",
    "path": "/",
    "sameSite": "no_restriction",
    "secure": false,
    "session": false,
    "storeId": "0",
    "value": "G0E9618868CF8EDDE288F63C4879A8AF5364669",
    "id": 6
  }
 */

public class CookieEntry {
    private String domain;
    private Double expirationDate;
    private Boolean hostOnly;
    private Boolean httpOnly;
    private String name;
    private String path;
    private String sameSite;
    private Boolean secure;
    private Boolean session;
    private String storeId;
    private String value;
    private Integer id;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Double getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Double expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getHostOnly() {
        return hostOnly;
    }

    public void setHostOnly(Boolean hostOnly) {
        this.hostOnly = hostOnly;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSameSite() {
        return sameSite;
    }

    public void setSameSite(String sameSite) {
        this.sameSite = sameSite;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getSession() {
        return session;
    }

    public void setSession(Boolean session) {
        this.session = session;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
