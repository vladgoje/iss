package persistence.HbmRepository;

import model.Bug;
import model.Verifier;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.BugRepositoryInterface;

import java.util.ArrayList;
import java.util.List;

public class BugRepository implements BugRepositoryInterface {
    DbUtils dbUtils;

    public BugRepository() {
        this.dbUtils = new DbUtils();
    }

    @Override
    public Bug findOne(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Bug bug = session.createQuery("from Bug b where b.id like :id", Bug.class).setParameter("id", aLong).setMaxResults(1).uniqueResult();
                tx.commit();
                return bug;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Iterable<Bug> findAll() {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<Bug> bugs = session.createQuery("from Bug", Bug.class).list();
                tx.commit();
                return bugs;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Bug save(Bug entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();
                return null;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public Bug delete(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Bug crit = session.createQuery("from Bug b where b.id like :id", Bug.class)
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
    public Bug update(Bug entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                Bug bug = session.load( Bug.class, entity.getId());
                bug.setName(entity.getName());
                bug.setDescription(entity.getDescription());
                bug.setPriority(entity.getPriority());
                session.update(bug);
                tx.commit();
                return null;
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public int count() {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                int count = (int) session.createQuery("select count(*) from Bug")
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
