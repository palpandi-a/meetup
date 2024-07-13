package com.util;

import com.persistence.util.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionUtil {

    private static final Logger LOGGER = Logger.getLogger(TransactionUtil.class.getName());

    public static <T> T execute(TransactionFunction<T> function) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactory.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T result = function.apply(session);
            transaction.commit();
            return result;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Exception occurred during transaction", ex);
            throw ex;
        }
    }

}
