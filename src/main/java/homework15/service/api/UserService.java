package homework15.service.api;

import homework15.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getCurrentUser();

    List<User> getDevelopers();
}
