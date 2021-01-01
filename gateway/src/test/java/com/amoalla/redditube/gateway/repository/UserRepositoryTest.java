package com.amoalla.redditube.gateway.repository;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    private static final String TEST_EMAIL = "user@mail.com";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExistsByEmail() {
        RedditubeUser user = new RedditubeUser();
        user.setUsername("username");
        user.setEmail(TEST_EMAIL);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setIdpId("id");

        entityManager.persist(user);

        assertTrue(userRepository.existsByEmail(TEST_EMAIL));
        assertFalse(userRepository.existsByEmail("other@mail.com"));
    }
}