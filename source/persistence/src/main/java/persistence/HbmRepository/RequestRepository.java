package persistence.HbmRepository;

import model.VerificationRequest;
import model.Verifier;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.RequestRepositoryInterface;

import java.util.ArrayList;
import java.util.List;

public class RequestRepository implements RequestRepositoryInterface {
    DbUtils dbUtils;

    public RequestRepository() {
        this.dbUtils = new DbUtils();
    }

    @Override
    public VerificationRequest findOne(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                VerificationRequest request = session.createQuery("from VerificationRequest b where b.id like :id", VerificationRequest.class).setParameter("id", aLong).setMaxResults(1).uniqueResult();
                tx.commit();
                return request;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Iterable<VerificationRequest> findAll() {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<VerificationRequest> requests = session.createQuery("from VerificationRequest", VerificationRequest.class).list();
                tx.commit();
                return requests;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public VerificationRequest save(VerificationRequest entity) {
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
    public VerificationRequest delete(Long aLong) {
        try(Session session = dbUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                VerificationRequest crit = session.createQuery("from VerificationRequest b where b.id like :id", VerificationRequest.class)
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
    public VerificationRequest update(VerificationRequest entity) {
        try(Session session = dbUtils.getSessionFactory().openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                VerificationRequest request = session.load( VerificationRequest.class, entity.getId());
                request.setBug(entity.getBug());
                request.setProgrammer(entity.getProgrammer());
                request.setStatus(entity.getStatus());
                session.update(request);
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
                int count = (int) session.createQuery("select count(*) from VerificationRequest")
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
