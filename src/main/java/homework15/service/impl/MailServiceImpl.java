package homework15.service.impl;

import homework15.exception.RecipientsException;
import homework15.model.User;
import homework15.model.enums.Topic;
import homework15.service.api.MailSender;
import homework15.service.api.MailService;
import homework15.service.api.MessageCreator;
import homework15.service.api.UserService;
import homework15.util.UserMailExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static homework15.model.enums.Topic.BUG;
import static homework15.model.enums.Topic.TASK;

public class MailServiceImpl implements MailService {

    private final Logger LOGGER = LogManager.getLogger(MailServiceImpl.class.getName());

    private final MessageCreator messageCreator;
    private final UserService userService;
    private final MailSender mailSender;

    public MailServiceImpl(MessageCreator messageCreator, UserService userService, MailSender mailSender) {
        this.messageCreator = messageCreator;
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Override
    public void sendMessageAboutBug() {
        List<User> developers = userService.getDevelopers();

        List<String> recipients = UserMailExtractor.getMailsFromUsers(developers);

        checkRecipients(recipients);

        String message = messageCreator.createMessage(recipients, BUG.getText());

        mailSender.sendMail(message);

        LOGGER.info("Message has been sent");
    }

    @Override
    public String sendFirstInvitation(User user) {
        String personalMessage = messageCreator.createPersonalMessage(user, TASK.getText());

        mailSender.sendMail(personalMessage);

        LOGGER.info("Personal message has been sent");

        return personalMessage;
    }

    @Override
    public void sendMeDummyMessagesForAllTopics() {
        User currentUser = userService.getCurrentUser()
                .orElseThrow(NoSuchElementException::new);
        Set<String> messages = getMessagesForUser(currentUser);
        LOGGER.warn("Message mustn't be empty");
        messages.forEach(mailSender::sendMail);
    }

    @Override
    public Map<String, User> getDeveloperEmails() {
        LOGGER.info("Get developer emails");
        return userService.getDevelopers().stream()
                .collect(Collectors.toMap(User::getEmail, Function.identity()));
    }

    private void checkRecipients(List<String> recipients) {
        if (recipients.isEmpty()) {
            throw new RecipientsException("Recipients list is empty.");
        }
    }

    private Set<String> getMessagesForUser(User currentUser) {
        return Arrays.stream(Topic.values())
                .map(Topic::getText)
                .map(text -> messageCreator.createPersonalMessage(currentUser, text))
                .collect(Collectors.toSet());
    }
}
