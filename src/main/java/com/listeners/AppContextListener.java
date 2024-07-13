package com.listeners;

import com.persistence.util.HibernateSessionFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.File;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String configFilePath = sce.getServletContext().getRealPath("/WEB-INF/conf/configuration.cfg.xml");
        HibernateSessionFactory.buildSessionFactory(new File(configFilePath));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateSessionFactory.shutDown();
    }

}
