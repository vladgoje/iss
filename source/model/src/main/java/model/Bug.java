package model;

import java.io.Serializable;

public class Bug implements Serializable {
    Long id;
    String name;
    String description;
    BugPriority priority;

    public Bug() { }

    public Bug(String name, String description, BugPriority priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Bug(Long id, String name, String description, BugPriority priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BugPriority getPriority() {
        return priority;
    }

    public void setPriority(BugPriority priority) {
        this.priority = priority;
    }
}
