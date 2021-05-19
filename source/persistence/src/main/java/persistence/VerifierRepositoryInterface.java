package persistence;

import model.Verifier;

public interface VerifierRepositoryInterface extends Repository<Long, Verifier>{
    Verifier findByUsername(String username);
    Verifier findByCredentials(String username, String password);
}
