package com.maxim.currencyexchanger.listeners;


import com.maxim.currencyexchanger.Utils.DatabaseConnectionPool;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConnectionPool.shutdown(); // закрытие пула соединений
    }
}
