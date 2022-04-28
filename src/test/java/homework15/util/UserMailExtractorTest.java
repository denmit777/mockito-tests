package homework15.util;

import homework15.model.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;

public class UserMailExtractorTest {

    private User user1;
    private User user2;
    private User user3;
    private String email1;
    private String email2;
    private String email3;

    @Before
    public void setUp() throws Exception {
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

        email1 = user1.getEmail();
        email2 = user2.getEmail();
        email3 = user3.getEmail();
    }

    @Test
    public void testGetMailsFromUsers() {
        List<String> expected = asList(email1, email2, email3);

        List<String> actual = UserMailExtractor.getMailsFromUsers(asList(user1, user2, user3));

        Assert.assertEquals(expected, actual);
    }
}