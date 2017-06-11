package org.devgateway.ocds.web.rest.controller;

import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.devgateway.toolkit.web.AbstractSpringDataRestControllerTest;
import org.devgateway.toolkit.web.TestUserDetailsConfiguration;
import org.devgateway.toolkit.web.spring.CustomRestMvcConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { CustomRestMvcConfiguration.class, TestUserDetailsConfiguration.class })
public class UserDashboardRestControllerTest extends AbstractSpringDataRestControllerTest {

    @Autowired
    private UserDashboardRestController userDashboardRestController;

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsAdminProcuringEntity")
    public void saveDashboardForCurrentUser() {

        UserDashboard ud = new UserDashboard();
        ud.setName("some name");
        ud.setFormUrlEncodedBody("some body");
        ResponseEntity<Void> responseEntity = userDashboardRestController.saveDashboardForCurrentUser(ud);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsAdminProcuringEntity")
    public void getDefaultDashboardForCurrentUserTest() {
        ResponseEntity<?> responseEntity =
                userDashboardRestController.getDefaultDashboardForCurrentUser(persistentEntityResourceAssembler);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "testUserDetailsAdminProcuringEntity")
    public void getDefaultDashboardsForCurrentUserTest() {
        PagedResources<Resource<UserDashboard>> responseEntity =
                userDashboardRestController.getDashboardsForCurrentUser(pageRequest, persistentEntityResourceAssembler);
    }
}
