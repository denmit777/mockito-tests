package homework15.util;

import homework15.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public final class UserMailExtractor {

    private final static Logger LOGGER = LogManager.getLogger(UserMailExtractor.class.getName());

    private UserMailExtractor() {
    }

    public static List<String> getMailsFromUsers(List<User> users) {
        LOGGER.info("Get mails from users");
        return users.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }
}
