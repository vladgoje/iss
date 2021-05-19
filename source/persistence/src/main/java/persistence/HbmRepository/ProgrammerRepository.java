package persistence.HbmRepository;

import model.Programmer;
import persistence.ProgrammerRepositoryInterface;

public class ProgrammerRepository implements ProgrammerRepositoryInterface {
    @Override
    public Programmer findOne(Long aLong) {
        return null;
    }

    @Override
    public Programmer findByUsername(String username) {
        return null;
    }

    @Override
    public Programmer findByCredentials(String username, String password) {
        return null;
    }

    @Override
    public Iterable<Programmer> findAll() {
        return null;
    }

    @Override
    public Programmer save(Programmer entity) {
        return null;
    }

    @Override
    public Programmer delete(Long aLong) {
        return null;
    }

    @Override
    public Programmer update(Programmer entity) {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }
}


