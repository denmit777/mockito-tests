package homework15.service.impl;

import homework15.exception.RecipientsException;
import homework15.model.User;
import homework15.service.api.MailSender;
import homework15.service.api.MessageCreator;
import homework15.service.api.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static homework15.model.enums.Topic.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceImplTest {

    private User user1;
    private User user2;
    private User user3;

    private String email1;
    private String email2;
    private String email3;

    private List<User> developers;
    private List<String> recipients;
    private List<String> recipientsEmpty;

    private String message;
    private String personalMessage;
    private Set<String> messages;

    private Map<String, User> developerEmails;

    @Mock
    private MessageCreator messageCreator;

    @Mock
    private UserService userService;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private MailServiceImpl service;

    @Before
    public void init() {
        user1 = new User();
        user1.setFirstName("Den");
        user1.setLastName("Ivanov");
        user1.setEmail("denIv@mai.ru");

        user2 = new User();
        user2.setFirstName("Ivan");
        user2.setLastName("Petrov");
        user2.setEmail("ivPet@mai.ru");

        user3 = new User();
        user3.setFirstName("Petr");
        user3.setLastName("Sidorov");
        user3.setEmail("pSid@mai.ru");

        developers = asList(user1, user2);

        email1 = user1.getEmail();
        email2 = user2.getEmail();
        email3 = user3.getEmail();

        recipients = asList(email1, email2);
        recipientsEmpty = asList("");

        message = "This is a message about bug";
        personalMessage = "This is a personal message";

        messages = new HashSet<>();
        messages.add(BUG.getText());
        messages.add(TASK.getText());
        messages.add(SERVER_OFFLINE.getText());

        developerEmails = new HashMap<>();
        developerEmails.put(email1, user1);
        developerEmails.put(email2, user2);
    }

    @Test
    public void testSendMessageAboutBug() {
        when(userService.getDevelopers()).thenReturn(developers);
        when(messageCreator.createMessage(recipients, BUG.getText())).thenReturn(message);
        doNothing().when(mailSender).sendMail(message);

        mailSender.sendMail(message);
        service.sendMessageAboutBug();

        verify(userService, times(1)).getDevelopers();
        verify(userService, never()).getCurrentUser();
        verify(messageCreator, times(1)).createMessage(recipients, BUG.getText());
        verify(messageCreator, never()).createPersonalMessage(user3, BUG.getText());
        verify(mailSender, times(2)).sendMail(message);
        verify(mailSender, never()).sendMail(personalMessage);
        verifyNoMoreInteractions(userService, messageCreator, mailSender);
    }

    @Test(expected = RecipientsException.class)
    public void testSendMessageAboutBugNegative_IfRecipientsIsEmpty() {
        when(messageCreator.createMessage(recipientsEmpty, BUG.getText())).thenThrow(RecipientsException.class);

        messageCreator.createMessage(recipientsEmpty, BUG.getText());
        service.sendMessageAboutBug();
    }

    @Test
    public void testSendFirstInvitation() {
        when(messageCreator.createPersonalMessage(user3, TASK.getText())).thenReturn(personalMessage);
        doNothing().when(mailSender).sendMail(personalMessage);
        final String expected = personalMessage;

        final String actual = service.sendFirstInvitation(user3);

        Assert.assertEquals(expected, actual);

        verify(messageCreator).createPersonalMessage(user3, TASK.getText());
        verify(messageCreator, times(1)).createPersonalMessage(user3, TASK.getText());
        verify(messageCreator, never()).createMessage(recipients, BUG.getText());
        verify(mailSender, times(1)).sendMail(personalMessage);
        verify(mailSender, never()).sendMail(message);
        verifyNoMoreInteractions(messageCreator, mailSender);
    }

    @Test
    public void testSendMeDummyMessagesForAllTopics() {
        when(userService.getCurrentUser()).thenReturn(java.util.Optional.ofNullable(user1));
        for (String message : messages) {
            when(messageCreator.createPersonalMessage(user1, message)).thenReturn(message);
        }
        for (String message : messages) {
            doNothing().when(mailSender).sendMail(message);
        }

        service.sendMeDummyMessagesForAllTopics();

        verify(userService, times(1)).getCurrentUser();
        for (String message : messages) {
            verify(messageCreator, times(1)).createPersonalMessage(user1, message);
        }
        for (String message : messages) {
            verify(mailSender, times(1)).sendMail(message);
        }
        verifyNoMoreInteractions(userService, messageCreator, mailSender);
    }

    @Test
    public void testGetDeveloperEmails() {
        when(userService.getDevelopers()).thenReturn(developers);
        final Map<String, User> expected = developerEmails;

        final Map<String, User> actual = service.getDeveloperEmails();

        Assert.assertEquals(expected, actual);

        verify(userService, times(1)).getDevelopers();
        verify(userService, never()).getCurrentUser();
    }
}