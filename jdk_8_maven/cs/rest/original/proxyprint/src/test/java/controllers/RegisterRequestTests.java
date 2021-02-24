package controllers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.repositories.AdminDAO;
import io.github.proxyprint.kitchen.models.repositories.ManagerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.security.web.FilterChainProxy;
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
public class RegisterRequestTests {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    private MockMvc mockMvc;

    @Autowired
    private AdminDAO admins;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private RegisterRequestDAO registerRequests;
    @Autowired
    private Gson GSON;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .addFilters(springSecurityFilterChain)
                .build();
        this.admins.deleteAll();
        this.registerRequests.deleteAll();
    }

    private RegisterRequest addRegisterRequest(RegisterRequest rr) throws Exception {

        String body = GSON.toJson(rr);

        MvcResult mvcResult = this.mockMvc.perform(post("/request/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);

        String responseText = mvcResult.getResponse().getContentAsString();

        RegisterRequest response = GSON.fromJson(responseText, RegisterRequest.class);

        //set ids to be equal
        rr.setId(response.getId());

        assert (rr.equals(response));

        return response;
    }

    //testar se o servidor regista pedido reprografia
    @Test
    public void registerRequestTest() throws Exception {
        RegisterRequest exampleRR = new RegisterRequest("manager", "manager", "proxyprint.pt.printshop@gmail.com", "1234", "address", 45d, 32d, "NIF", "name", false);
        addRegisterRequest(exampleRR);
    }

    @Test
    public void acceptRegisterRequest() throws Exception {
        //test admin
        Admin admin = admins.save(new Admin("admin", "admin", "admin@mail.pt"));

        //nr of pshops before
        int pshopsBefore = (int) this.printshops.count();

        RegisterRequest exampleRR = new RegisterRequest("manager", "manager", "proxyprint.pt.printshop@gmail.com", "1234", "address", 45d, 32d, "NIF", "name", false);
        RegisterRequest rr = addRegisterRequest(exampleRR);

        MvcResult mvcResult = this.mockMvc.perform(post("/request/accept/" + (int) rr.getId())
                .with(httpBasic("admin", "admin"))).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);

        String response = mvcResult.getResponse().getContentAsString();

        JsonObject jsonObject = (new JsonParser()).parse(response).getAsJsonObject();

        boolean status = jsonObject.get("success").getAsBoolean();

        assert (status);

        int pshopsAfter = (int) this.printshops.count();

        assert (pshopsBefore == pshopsAfter - 1);

        Manager m = this.managers.findByUsername("manager");
        assert (m.getPassword().equals("1234"));
    }

    @Test
    public void pendingRequestTest() throws Exception {
        //test admin
        Admin admin = admins.save(new Admin("admin", "admin", "admin@mail.pt"));

        //adicionar direto aos daos neste caso
        RegisterRequest exampleRR = new RegisterRequest("manager1", "manager1", "proxyprint.pt.printshop@gmail.com", "1234", "address", 45d, 32d, "NIF", "name", false);
        RegisterRequest exampleRR1 = new RegisterRequest("manager", "manager", "proxyprint.pt.printshop@gmail.com", "1234", "address", 45d, 32d, "NIF", "name", true);
        this.registerRequests.save(exampleRR);
        this.registerRequests.save(exampleRR1);

        MvcResult mvcResult = this.mockMvc.perform(get("/requests/pending")
                .with(httpBasic("admin", "admin"))).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);

        String response = mvcResult.getResponse().getContentAsString();

        Type listOfRR = new TypeToken<List<RegisterRequest>>(){}.getType();
        
        List<RegisterRequest> pending1 = this.registerRequests.findByAccepted(false);
        List<RegisterRequest> pending2 = GSON.fromJson(response, listOfRR);

        System.out.println(pending1);
        System.out.println(pending2);
        
        assert (CollectionUtils.isEqualCollection(pending1, pending2));
    }
}
