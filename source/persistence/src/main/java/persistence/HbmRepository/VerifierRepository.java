package persistence.HbmRepository;

import model.Verifier;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.VerifierRepositoryInterface;

import java.util.ArrayList;
import java.util.List;

public class VerifierRepository implements VerifierRepositoryInterface {
    DbUtils dbUtils;

    public VerifierRepository() {
        this.dbUtils = new DbUtils();
    }

    @Override
    public Verifier findOne(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Verifier verifier = session.createQuery("from Verifier v where v.id like :id", Verifier.class).setParameter("id", aLong).setMaxResults(1).uniqueResult();
                tx.commit();
                return verifier;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Verifier findByUsername(String username) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Verifier verifier = session.createQuery("from Verifier v where v.username like :username", Verifier.class).setParameter("username", username).setMaxResults(1).uniqueResult();
                tx.commit();
                return verifier;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Verifier findByCredentials(String username, String password) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Verifier verifier = session.createQuery("from Verifier v where v.username like :username and v.password like :password", Verifier.class)
                        .setParameter("username", username)
                        .setParameter("password", password)
                        .setMaxResults(1).uniqueResult();
                tx.commit();
                return verifier;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Iterable<Verifier> findAll() {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<Verifier> verifiers = session.createQuery("from Verifier", Verifier.class).list();
                tx.commit();
                return verifiers;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Verifier save(Verifier entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();

                tx = session.beginTransaction();
                List<Verifier> verifiers = session.createQuery("FROM Verifier v ORDER BY v.id DESC", Verifier.class).setMaxResults(1).list();
                tx.commit();
                return verifiers.get(0);
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Verifier delete(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Verifier crit = session.createQuery("from Verifier where v.id like :id", Verifier.class)
                        .setParameter("id", aLong)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(crit);
                tx.commit();
                return crit;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Verifier update(Verifier entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                Verifier verifier = session.load( Verifier.class, entity.getId());
                verifier.setUsername(entity.getUsername());
                verifier.setPassword(entity.getPassword());
                verifier.setSalary(entity.getSalary());
                session.update(verifier);
                tx.commit();
                return entity;
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public int count() {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                int count = (int) session.createQuery("select count(*) from Verifier")
                        .setMaxResults(1).uniqueResult();
                tx.commit();
                return count;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return 0;
    }

    @Override
    public boolean exists(Long id) {
        return findOne(id) != null;
    }

}
