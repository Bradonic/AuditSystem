package com.group12.controller.account;

/**  Author: Rachael Klein
 *   Student no: 218 057 377
 *   Date: 21-09-2020
 *   Description: Controller Test for UserAccount
 */

import com.group12.entity.UserAccount;
import com.group12.factory.UserAccountFactory;
import com.group12.service.account.UserAccountService;
import com.group12.util.GenerateID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAccountControllerTest {

    static LocalDate date = LocalDate.now();

    //test your rest application through restTemplate
    @Autowired
    private TestRestTemplate restTemplate;

    //need a base URl
    private static String baseURL = "http://localhost:8080/userAccount";

    private static String userAccountId;

    @Test
    public void a_create() {
        try {
            UserAccount userAccount = UserAccountFactory.createUserAccount("rachael@cput.ac.za", "P@ssword123", true, date);
            String url = baseURL + "/create";
            System.out.println(url);
            ResponseEntity<UserAccount> postResponse = restTemplate.postForEntity(url, userAccount, UserAccount.class);
            assertNotNull(postResponse);
            assertNotNull(postResponse.getBody());
            System.out.println(postResponse.getBody());

            userAccountId = postResponse.getBody().getUserId();
        }catch (Exception e)
        {
            fail();
        }
    }

    @Test
    public void b_read() {
        try {
            String url = baseURL + "/read?userId=" +  userAccountId;
            System.out.println("URL: " + url);

            ResponseEntity<UserAccount> accountResponse = restTemplate.getForEntity(url, UserAccount.class);
            assertEquals(userAccountId, accountResponse.getBody().getUserId());
        }catch(Exception e)
        {
            fail();
        }
    }

    @Test
    public void c_update() {
        try{
            // First get the existing user by it's ID
            String getUrl = baseURL + "/read?userId=" +  userAccountId;
            System.out.println("URL: " + getUrl);
            ResponseEntity<UserAccount> accountResponse = restTemplate.getForEntity(getUrl, UserAccount.class);
            UserAccount userAccount = accountResponse.getBody();

            // Update the existing user
            UserAccount updated = new UserAccount.Builder().copy(userAccount).setEmail("RachaelJoubert@gmail.com").build();
            String updateUrl = baseURL + "/update";
            System.out.println("URL: " + updateUrl);
            System.out.println("Put data: " + updated);
            HttpEntity<UserAccount> httpsEntityUserAccount = new HttpEntity<UserAccount>(updated, null);
            ResponseEntity<UserAccount> updateResponse = restTemplate.exchange(updateUrl, HttpMethod.PUT, httpsEntityUserAccount, UserAccount.class);

            assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            assertEquals(updated.getEmail(), updateResponse.getBody().getEmail());
        }catch (Exception e)
        {
            fail();
        }
    }

    @Test
    public void d_registerUserAccount() {
        String email = "rachaelRegister@cput.ac.za";
        String url = baseURL + "/registerUserAccount?email="+email+"&verifyemail="+email+"&password=P@ssword123&verifypassword=P@ssword123";
        System.out.println("URL: " + url);
        ResponseEntity<UserAccount> registerResponse = restTemplate.postForEntity(url, null, UserAccount.class);
        assertEquals(email, registerResponse.getBody().getEmail());
    }

    @Test
    public void e_changePassword() {
        try{
            String url = baseURL + "/changePassword?email=RachaelJoubert@gmail.com&existingPassword=P@ssword123&newPassword=123P@ssword&verifyNewPassword=123P@ssword";
            System.out.println("URL: " + url);
            ResponseEntity updateResponse = restTemplate.exchange(url, HttpMethod.PUT, null, boolean.class);
            assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            assertTrue((boolean)updateResponse.getBody());
        }catch (Exception e)
        {
            fail();
        }
    }

    @Test
    public void f_updateEmailAddress() {
        try{
            // the update email address end point takes in multiple param. seperated by a & symbol.
            String url = baseURL + "/updateEmailAddress?email=RachaelJoubert@gmail.com&existingPassword=123P@ssword&newEmail=rachaelKlein@cput.ac.za&verifyNewEmail=rachaelKlein@cput.ac.za";
            System.out.println("URL: " + url);
            ResponseEntity updateResponse = restTemplate.exchange(url, HttpMethod.PUT, null, boolean.class);
            assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            assertTrue((boolean)updateResponse.getBody());
        }catch (Exception e)
        {
            fail();
        }
    }

    @Test
    public void g_forgotPassword() {
        try {
            String url = baseURL + "/forgotPassword?email=rachaelKlein@cput.ac.za";
            System.out.println("URL: " + url);
            ResponseEntity accountResponse = restTemplate.getForEntity(url, boolean.class);
            assertEquals(HttpStatus.OK, accountResponse.getStatusCode());
            assertEquals(true, accountResponse.getBody());
        }catch(Exception e)
        {
            fail();
        }
    }

    @Test
    public void h_getUserAccountViaEmailAddress() {
        try {
            String url = baseURL + "/getUserAccountViaEmailAddress?email=rachaelKlein@cput.ac.za";
            System.out.println("URL: " + url);
            ResponseEntity<UserAccount> accountResponse = restTemplate.getForEntity(url, UserAccount.class);
            assertEquals(HttpStatus.OK, accountResponse.getStatusCode());
            assertEquals("rachaelKlein@cput.ac.za", accountResponse.getBody().getEmail());
        }catch(Exception e)
        {
            fail();
        }
    }

    @Test
    public void i_getAll() {
        String url = baseURL + "/all";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response= restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println(response);
        System.out.println(response.getBody());
    }
    @Test
    public void j_delete() {
        try{
            String url = baseURL + "/delete?userId=" + userAccountId;
            System.out.println("URL: " + url);
            ResponseEntity updateResponse = restTemplate.exchange(url, HttpMethod.DELETE, null, boolean.class);
            assertEquals(updateResponse.getStatusCode(), HttpStatus.OK);
            assertTrue((boolean)updateResponse.getBody());
        }catch (Exception e)
        {
            fail();
        }
    }
}