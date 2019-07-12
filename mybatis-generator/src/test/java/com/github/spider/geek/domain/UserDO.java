package com.github.spider.geek.domain;

import java.io.Serializable;

 /** create by system from table user(测试表)  */
public class UserDO implements Serializable {
    //主键(id)
    private Integer id;

    //登陆名(username)
    private String username;

    //密码(password)
    private String password;

    //城市(city)
    private String city;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }
}