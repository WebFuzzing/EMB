/**
 * 
 */
package org.devgateway.ocds.forms.wicket.providers;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.devgateway.ocds.persistence.repository.UserDashboardRepository;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.web.security.SecurityUtil;
import org.devgateway.toolkit.forms.wicket.providers.SortableJpaRepositoryDataProvider;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * @author mpost
 *
 */
public class PersonDashboardJpaRepositoryProvider extends SortableJpaRepositoryDataProvider<UserDashboard> {

    private static final long serialVersionUID = -490237568464403107L;

    private UserDashboardRepository userDashboardRepository;

    private PersonRepository personRepository;

    public PersonDashboardJpaRepositoryProvider(UserDashboardRepository jpaRepository,
            PersonRepository personRepository) {
        super(jpaRepository);
        this.personRepository = personRepository;
        userDashboardRepository = (UserDashboardRepository) jpaRepository;
    }

    /**
     * @see SortableDataProvider#iterator(long, long)
     */
    @Override
    public Iterator<UserDashboard> iterator(final long first, final long count) {
        int page = (int) ((double) first / WebConstants.PAGE_SIZE);
        Page<UserDashboard> findAll =
                userDashboardRepository.findDashboardsForPersonId(SecurityUtil.getCurrentAuthenticatedPerson().getId(),
                        new PageRequest(page, WebConstants.PAGE_SIZE, translateSort()));
        return findAll.iterator();
    }

    @Override
    public long size() {
        return personRepository.getOne(SecurityUtil.getCurrentAuthenticatedPerson().getId()).getDashboards().size();
    }

}
