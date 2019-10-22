/**
 * 
 */
package org.devgateway.toolkit.web;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.devgateway.ocds.web.rest.controller.request.GenericPagingRequest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author mpostelnicuThis helps us test Spring Data Rest
 *         repositories with our infrastructure.
 */
@Transactional
public abstract class AbstractSpringDataRestControllerTest extends AbstractWebTest {

    protected PersistentEntityResourceAssembler persistentEntityResourceAssembler;

    protected PageRequest pageRequest;

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mockMvc;

    /**
     * http://stackoverflow.com/a/36960968 
     * This is needed if you do
     * {@link PagedResourcesAssembler#toResource(org.springframework.data.domain.Page)}
     * in your controller
     */
    private void mockHttpServletRequestForResouceAssemblerSupport() {
        String localHost = "http://localhost";
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getRequestURL()).thenReturn(new StringBuffer(localHost));
        when(httpServletRequestMock.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(httpServletRequestMock.getRequestURI()).thenReturn(localHost);
        when(httpServletRequestMock.getContextPath()).thenReturn(StringUtils.EMPTY);
        when(httpServletRequestMock.getServletPath()).thenReturn(StringUtils.EMPTY);
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        this.persistentEntityResourceAssembler = mock(PersistentEntityResourceAssembler.class);
        this.pageRequest = new PageRequest(0, GenericPagingRequest.DEFAULT_PAGE_SIZE);

        mockHttpServletRequestForResouceAssemblerSupport();
    }

}
