package persistence;

import model.Bug;
import model.BugSolver;

public interface BugRepositoryInterface extends Repository<Long, Bug>{
    void addBugSolver(BugSolver solver);
    void removeBugSolver(BugSolver solver);
    boolean existsSolver(BugSolver solver);
}
