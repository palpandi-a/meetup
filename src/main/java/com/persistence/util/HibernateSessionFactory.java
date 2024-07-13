package com.persistence.util;

import com.exception.InitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateSessionFactory {

    private static final Logger LOGGER = Logger.getLogger(HibernateSessionFactory.class.getName());

    private static SessionFactory sessionFactory;

    public static synchronized void buildSessionFactory(File configFile) {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .configure(configFile)
                        .build();
                MetadataSources metadataSources = new MetadataSources(serviceRegistry);
                Metadata metadata = metadataSources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Exception occurred while building session factory", ex);
                throw new InitializationException(ex);
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new InitializationException("SessionFactory is not initialized. Call buildSessionFactory() first.");
        }
        return sessionFactory;
    }

    public static void shutDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

}
