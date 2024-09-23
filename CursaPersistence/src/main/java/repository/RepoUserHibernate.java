package repository;

import model.User;
import org.hibernate.Session;

public class RepoUserHibernate implements UserRepository{
    @Override
    public User save(User user){
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(user));
        return null;

    }
    @Override
    public void delete(Long id) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            User message=session.createQuery("from User where id=?1",User.class).
                    setParameter(1,id).uniqueResult();
            System.out.println("In delete am gasit mesajul "+message);
            if (message!=null) {
                session.remove(message);
                session.flush();
            }
        });

    }

    @Override
    public User findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from User where id = :userId", User.class)
                    .setParameter("userId", id)
                    .uniqueResult();
        }
    }
    @Override
    public void update(User user) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            User existingUser = session.find(User.class, user.getId());
            if (existingUser != null) {
                System.out.println("În metoda update, am gasit utilizatorul cu id-ul " + user.getId());
                existingUser.setUsername(user.getUsername());
                existingUser.setPassword(user.getPassword());
                session.merge(existingUser);
                session.flush();
            }
        });
    }
    @Override
    public User findByUsernamePassword(String username, String password) {
        System.out.println("Aici suntem");
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from User where username = :username and password = :password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .setMaxResults(1)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<User> findAll(){
        return null;
    }



}
