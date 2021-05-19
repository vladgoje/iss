package model;

import java.io.Serializable;

public class Employee implements Serializable {
    Long id;
    String username;
    String password;
    Float salary;

    public Employee() {
    }

    public Employee(Long id, String username, String password, Float salary) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salary = salary;
    }

    public Employee(String username, String password) {
        this.username = username;
        this.password = password;
        this.salary = -1F;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }
}
