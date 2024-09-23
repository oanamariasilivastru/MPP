package model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User extends model.Entity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public User(){
        id = 0l;
        username = "default";
        password = "default";
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    @Id
    @GeneratedValue(generator = "increment")
    public Long getId(){
        return this.id;
    }
    @Override
    public void setId(Long id){
        this.id = id;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getPassword(), user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword());
    }

    @Override
    public String toString() {
        return "User{Id:" + getId() +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
