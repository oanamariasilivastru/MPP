package repository;

import model.Entity;
import model.User;

public interface UserRepository extends Repository<Long, User>{
    public User findByUsernamePassword(String username, String password);
}
