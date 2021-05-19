package persistence;

import model.Programmer;
import model.Verifier;

public interface ProgrammerRepositoryInterface extends Repository<Long, Programmer>{
    Programmer findByUsername(String username);
    Programmer findByCredentials(String username, String password);
}
