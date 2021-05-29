package model;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Programmer extends Employee implements Serializable {

    @OneToMany(
            mappedBy = "programmer",
            cascade = CascadeType.ALL
    )
    private List<VerificationRequest> requests = new ArrayList<>();

    private int score;
    private int fixedBugs;

    public Programmer() {
    }

    public Programmer(String username, String password){
        super(username, password);
    }

    public Programmer(Long id, String username, String password) {
        super(id, username, password, 0F);
    }

    public Programmer(Long id, String username, String password, Float salary) {
        super(id, username, password, salary);
    }

    public Programmer(Long id, String username, String password, Float salary, List<VerificationRequest> requests){
        super(id, username, password, salary);
        this.requests = requests;
    }

    public Programmer(Long id, String username, String password, Float salary, List<VerificationRequest> requests, int score, int fixedBugs){
        super(id, username, password, salary);
        this.requests = requests;
        this.score = score;
        this.fixedBugs = fixedBugs;
    }

    public List<VerificationRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<VerificationRequest> requests) {
        this.requests = requests;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFixedBugs() {
        return fixedBugs;
    }

    public void setFixedBugs(int fixedBugs) {
        this.fixedBugs = fixedBugs;
    }
}
