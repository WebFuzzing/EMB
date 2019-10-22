package org.devgateway.toolkit.forms.wicket.components.table;

import org.apache.commons.lang3.StringUtils;
import org.devgateway.toolkit.persistence.dao.TestForm;
import org.devgateway.toolkit.persistence.dao.TestForm_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octavian on 03.07.2016.
 */
public class TestFormFilterState extends JpaFilterState<TestForm> {

    private static final long serialVersionUID = 8005371716983257722L;
    private String textField;

    @Override
    public Specification<TestForm> getSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(textField)) {
                predicates.add(cb.like(root.get(TestForm_.textField), "%" + textField + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(final String textField) {
        this.textField = textField;
    }
}
