/*
 * Copyright 2016 Jorge Caldas, José Cortez
 * José Francisco, Marcelo Gonçalves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author josesousa
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements Serializable {

    public static enum Roles {
        ROLE_USER, ROLE_EMPLOYEE, ROLE_MANAGER, ROLE_ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    @Column(name = "username", nullable = false, unique = true)
    protected String username;
    @JsonIgnore
    @Column(name = "password", nullable = false)
    protected String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Cascade(CascadeType.ALL)
    protected Set<String> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<GrantedAuthority> getRoles() {
        List<GrantedAuthority> authorities = new LinkedList<>();
        for (String a : roles) {
            authorities.add(new SimpleGrantedAuthority(a));
        }
        return authorities;
    }

    public Set<String> getRolesSet() { return this.roles; }

    protected void addRole(String role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", password=" + password + ", roles=" + roles + '}';
    }

}
