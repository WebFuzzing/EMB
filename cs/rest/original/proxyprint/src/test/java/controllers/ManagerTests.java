package controllers;

import com.google.gson.Gson;
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.AdminDAO;
import io.github.proxyprint.kitchen.models.repositories.ManagerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by daniel on 20-06-2016.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WebAppConfig.class)
@WebIntegrationTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManagerTests {

    private static boolean SEEDED = false;

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
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .addFilters(springSecurityFilterChain)
                .build();
        /*this.admins.deleteAll();
        this.managers.deleteAll();
        this.printshops.deleteAll();
        this.registerRequests.deleteAll();*/
        if (!SEEDED) {
            SEEDED = true;
            this.mockMvc.perform(post("/admin/seed")).andReturn();
        }
    }

    @Test
    public void _1addEmployee() throws Exception {
        Manager m = managers.findAll().iterator().next();
        PrintShop pshop = m.getPrintShop();
        long id = pshop.getId();

        Employee e = new Employee("jdc", "1234", "Daniel Caldas", pshop);
        String body = GSON.toJson(e);

        MvcResult mvcResult = this.mockMvc.perform(post("/printshops/" + id + "/employees").with(httpBasic(m.getUsername(), m.getPassword()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);

        String responseText = mvcResult.getResponse().getContentAsString();

        Map response = GSON.fromJson(responseText, Map.class);

        assert (response.get("success").equals(true));
    }

    @Test
    public void _2getEmployee() throws Exception {
        Manager m = managers.findAll().iterator().next();
        PrintShop pshop = m.getPrintShop();
        long id = pshop.getId();

        MvcResult mvcResult = this.mockMvc.perform(get("/printshops/" + id + "/employees").with(httpBasic(m.getUsername(), m.getPassword()))).andReturn();

        assert (mvcResult.getResponse().getStatus() == 200);
    }

}
