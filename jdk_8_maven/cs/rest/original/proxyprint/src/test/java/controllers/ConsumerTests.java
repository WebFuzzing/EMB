package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WebAppConfig.class)
@WebIntegrationTest
public class ConsumerTests {

    @Autowired
    WebApplicationContext wac;
    @Autowired
    private UserDAO users;
    @Autowired
    private Gson GSON;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    //testar se o servidor regista utilizador
    @Test
    public void registerUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/consumer/register")
                .param("username", "testusername")
                .param("password", "testpassword")
                .param("name", "testname")
                .param("email", "testemail@mail.pt")
                .param("latitude", "testlat")
                .param("longitude", "testlong")).andReturn();
        

        String response = mvcResult.getResponse().getContentAsString();

        JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();

        boolean status = jsonObject.get("success").getAsBoolean();

        assert (status);
    }

    //testar login
    @Test
    public void login() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/login")
                .param("username", "testusername")
                .param("password", "testpassword")).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();

        boolean status = jsonObject.get("success").getAsBoolean();

        assert (status);
    }

    //testar se o servidor deteta utilizador jÃ¡ registado
    @Test
    public void errorRegisterUser() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/consumer/register")
                .param("username", "testusername")
                .param("password", "testpassword")
                .param("name", "testname")
                .param("email", "testemail@mail.pt")
                .param("latitude", "testlat")
                .param("longitude", "testlong")).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();

        boolean status = !jsonObject.get("success").getAsBoolean();

        assert (status);

        User user = this.users.findByUsername("testusername");
        this.users.delete(user);
    }
}
