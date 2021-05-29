package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table( name = "requests" )
public class VerificationRequest implements Serializable {
    Long id;


    @ManyToOne
    @JoinColumn(name="bug", nullable=false)
    private Bug bug;

    @ManyToOne
    @JoinColumn(name="programmer", nullable=false)
    private Programmer programmer;

    private RequestStatus status;

    public VerificationRequest(){}

    public VerificationRequest(Bug bug, Programmer programmer, RequestStatus status) {
        this.bug = bug;
        this.programmer = programmer;
        this.status = status;
    }

    public VerificationRequest(Long id, Bug bug, Programmer programmer, RequestStatus status) {
        this.id = id;
        this.bug = bug;
        this.programmer = programmer;
        this.status = status;
    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bug getBug() {
        return bug;
    }

    public void setBug(Bug bug) {
        this.bug = bug;
    }

    public Programmer getProgrammer() {
        return programmer;
    }

    public void setProgrammer(Programmer programmer) {
        this.programmer = programmer;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
