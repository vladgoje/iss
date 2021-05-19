package model;

import java.io.Serializable;

public class Programmer extends Employee implements Serializable {
    public Programmer(Long id, String username, String password, Float salary) {
        super(id, username, password, salary);
    }

    public Programmer(String password, String username){
        super(username, password);
    }
}
