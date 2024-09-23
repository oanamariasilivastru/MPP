package repository;

import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtils {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(){
        if ((sessionFactory==null)||(sessionFactory.isClosed()))
            sessionFactory=createNewSessionFactory();
        return sessionFactory;
    }

    private static SessionFactory createNewSessionFactory() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(User.class)
                .setProperty("hibernate.connection.url", "jdbc:sqlite:C:\\Users\\Oana\\Desktop\\LabMPP\\curse.db")
                //.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect")
                .buildSessionFactory();
        return sessionFactory;
    }


    public static  void closeSessionFactory(){
        if (sessionFactory!=null)
            sessionFactory.close();
    }
}
