package persistence;

import model.Verifier;

public interface VerifierRepositoryInterface extends Repository<Long, Verifier>{
    public Verifier findByUsername(String username);
}
