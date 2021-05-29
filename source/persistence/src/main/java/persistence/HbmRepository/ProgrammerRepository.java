package persistence.HbmRepository;

import model.Programmer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.ProgrammerRepositoryInterface;

import java.util.ArrayList;
import java.util.List;

public class ProgrammerRepository implements ProgrammerRepositoryInterface {
    DbUtils dbUtils;

    public ProgrammerRepository() {
        this.dbUtils = new DbUtils();
    }

    @Override
    public Programmer findOne(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Programmer Programmer = session.createQuery("from Programmer p where p.id = :id", Programmer.class).setParameter("id", aLong).setMaxResults(1).uniqueResult();
                tx.commit();
                return Programmer;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Programmer findByUsername(String username) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Programmer Programmer = session.createQuery("from Programmer p where p.username like :username", Programmer.class).setParameter("username", username).setMaxResults(1).uniqueResult();
                tx.commit();
                return Programmer;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Programmer findByCredentials(String username, String password) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Programmer Programmer = session.createQuery("from Programmer p where p.username like :username and p.password like :password", Programmer.class)
                        .setParameter("username", username)
                        .setParameter("password", password)
                        .setMaxResults(1).uniqueResult();
                tx.commit();
                return Programmer;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Iterable<Programmer> findAll() {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<Programmer> programmers = session.createQuery("from Programmer", Programmer.class).list();
                tx.commit();
                return programmers;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Programmer save(Programmer entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();
                return entity;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Programmer delete(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Programmer crit = session.createQuery("from Programmer p where p.id = :id", Programmer.class)
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
    public Programmer update(Programmer entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                Programmer Programmer = session.load( Programmer.class, entity.getId());
                Programmer.setUsername(entity.getUsername());
                Programmer.setPassword(entity.getPassword());
                Programmer.setSalary(entity.getSalary());
                Programmer.setFixedBugs(entity.getFixedBugs());
                Programmer.setScore(entity.getScore());
                session.update(Programmer);
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
                int count = (int) session.createQuery("select count(*) from Programmer")
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
