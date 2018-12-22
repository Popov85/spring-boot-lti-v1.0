package ua.edu.ratos.edx.domain;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class User {

    private Long userId = 1L;

    @NotEmpty(message = "Name is a required field")
    @Length(min = 2, message = "The field must be at least 2 characters")
    private String name;

    @NotEmpty(message = "Surname is a required field")
    private String surname;

    @NotEmpty(message = "Email is a required field")
    private String email;

    @NotEmpty(message = "Password is a required field")
    @Size(min=8, max=16, message = "Password must have from 8 to 16 characters!")
    private char[] password;

    private Set<String> roles = new HashSet<>(Arrays.asList("ROLE_STUDENT"));

    private boolean active = true;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                '}';
    }
}
