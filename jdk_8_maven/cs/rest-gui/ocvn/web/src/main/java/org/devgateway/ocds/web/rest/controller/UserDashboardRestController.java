package org.devgateway.ocds.web.rest.controller;

import javax.validation.Valid;

import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.devgateway.ocds.persistence.repository.UserDashboardRepository;
import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
public class UserDashboardRestController {

    private UserDashboardRepository repository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PagedResourcesAssembler<UserDashboard> resourcesAssembler;
    

    @Autowired
    public UserDashboardRestController(UserDashboardRepository repo) {
        repository = repo;
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },
            value = "/userDashboards/search/getDefaultDashboardForCurrentUser")
    @PreAuthorize("hasRole('ROLE_PROCURING_ENTITY')")
    @ResponseBody
    public ResponseEntity<?>
            getDefaultDashboardForCurrentUser(PersistentEntityResourceAssembler persistentEntityResourceAssembler) {
        UserDashboard dashboard = repository.getDefaultDashboardForPersonId(getCurrentAuthenticatedPerson().getId());
        if (dashboard == null) {
            return ResponseEntity.ok().build();
        }
        Resource<Object> resource = persistentEntityResourceAssembler.toResource(dashboard);
        return ResponseEntity.ok(resource);
    }
    
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },
            value = "/userDashboards/getCurrentAuthenticatedUserDetails")
    @ResponseBody
    public ResponseEntity<?>
            getCurrentAuthenticatedUserDetails(PersistentEntityResourceAssembler persistentEntityResourceAssembler) {

        Person currentAuthenticatedPersonToken = getCurrentAuthenticatedPerson();
        Person currentAuthenticatedPerson;
        if (currentAuthenticatedPersonToken != null) {
            currentAuthenticatedPerson = personRepository.getOne(currentAuthenticatedPersonToken.getId());
        } else {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok(currentAuthenticatedPerson);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },
            value = "/userDashboards/search/getDashboardsForCurrentUser")
    @PreAuthorize("hasRole('ROLE_PROCURING_ENTITY')")
    @ResponseBody
    public  PagedResources<Resource<UserDashboard>> getDashboardsForCurrentUser(Pageable page,
            PersistentEntityResourceAssembler persistentEntityResourceAssembler) {
        return resourcesAssembler.toResource(
                repository.findDashboardsForPersonId(getCurrentAuthenticatedPerson().getId(), page),
                (ResourceAssembler) persistentEntityResourceAssembler);
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET },
            value = "/userDashboards/saveDashboardForCurrentUser")
    @PreAuthorize("hasRole('ROLE_PROCURING_ENTITY')")
    public ResponseEntity<Void> saveDashboardForCurrentUser(@ModelAttribute @Valid UserDashboard userDashboard) {
        Person person = personRepository.getOne(getCurrentAuthenticatedPerson().getId());
        userDashboard.getUsers().add(person);
        person.getDashboards().add(userDashboard);
        repository.save(userDashboard);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/userDashboards/saveDashboard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> saveDashboard(@ModelAttribute @Valid UserDashboard userDashboard) {
        repository.save(userDashboard);
        return ResponseEntity.ok().build();
    }

    private static Person getCurrentAuthenticatedPerson() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        final Object principal = authentication.getPrincipal();
        if (principal instanceof Person) {
            return (Person) principal;
        }
        return null;
    }

}