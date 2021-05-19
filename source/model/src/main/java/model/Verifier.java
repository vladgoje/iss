package model;

import java.io.Serializable;

public class Verifier extends Employee implements Serializable {
    public Verifier(Long id, String username, String password, Float salary) {
        super(id, username, password, salary);
    }

    public Verifier(String username, String password){
        super(username, password);
    }

    public Verifier() {
        super();
    }
}
