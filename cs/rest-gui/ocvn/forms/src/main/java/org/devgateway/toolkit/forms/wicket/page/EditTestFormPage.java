/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.devgateway.toolkit.forms.wicket.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.devgateway.toolkit.forms.wicket.components.form.CheckBoxBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.CheckBoxPickerBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.CheckBoxToggleBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.DateFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.DateTimeFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.FileInputBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.Select2ChoiceBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.Select2MultiChoiceBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.SummernoteBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.TextAreaFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.TextFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.page.edit.AbstractEditPage;
import org.devgateway.toolkit.forms.wicket.page.lists.ListTestFormPage;
import org.devgateway.toolkit.forms.wicket.providers.GenericChoiceProvider;
import org.devgateway.toolkit.forms.wicket.providers.GenericPersistableJpaRepositoryTextChoiceProvider;
import org.devgateway.toolkit.persistence.dao.TestForm;
import org.devgateway.toolkit.persistence.dao.categories.Group;
import org.devgateway.toolkit.persistence.dao.categories.Role;
import org.devgateway.toolkit.persistence.repository.GroupRepository;
import org.devgateway.toolkit.persistence.repository.RoleRepository;
import org.devgateway.toolkit.persistence.repository.TestFormRepository;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author mpostelnicu
 *
 */

@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_USER)
@MountPath("/editTestForm")
public class EditTestFormPage extends AbstractEditPage<TestForm> {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private TestFormRepository testComponentsRepository;

    @SpringBean
    private RoleRepository roleRepository;

    @SpringBean
    private GroupRepository groupRepository;

    /**
     * @param parameters
     */
    public EditTestFormPage(final PageParameters parameters) {
        super(parameters);

        this.jpaRepository = testComponentsRepository;
        this.listPageClass = ListTestFormPage.class;
    }

    @Override
    protected TestForm newInstance() {
        return new TestForm();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        TextFieldBootstrapFormComponent<String> textField = new TextFieldBootstrapFormComponent<>("textField");
        textField.required();
        editForm.add(textField);

        TextAreaFieldBootstrapFormComponent<String> textArea = new TextAreaFieldBootstrapFormComponent<>("textArea");
        textArea.required();
        editForm.add(textArea);

        SummernoteBootstrapFormComponent summernote = new SummernoteBootstrapFormComponent("summernote");
        summernote.required();
        editForm.add(summernote);

        Select2ChoiceBootstrapFormComponent<Group> entitySelect = new Select2ChoiceBootstrapFormComponent<Group>(
                "entitySelect", new GenericPersistableJpaRepositoryTextChoiceProvider<Group>(groupRepository));
        entitySelect.required();
        editForm.add(entitySelect);

        Select2MultiChoiceBootstrapFormComponent<Role> entityMultiSelect =
                new Select2MultiChoiceBootstrapFormComponent<Role>("entityMultiSelect",
                        new GenericPersistableJpaRepositoryTextChoiceProvider<Role>(roleRepository));
        entityMultiSelect.required();
        editForm.add(entityMultiSelect);

        CheckBoxBootstrapFormComponent checkbox = new CheckBoxBootstrapFormComponent("checkbox");
        checkbox.required();
        editForm.add(checkbox);

        CheckBoxPickerBootstrapFormComponent checkboxPicker =
                new CheckBoxPickerBootstrapFormComponent("checkboxPicker");
        checkboxPicker.required();
        editForm.add(checkboxPicker);

        CheckBoxToggleBootstrapFormComponent checkboxToggle =
                new CheckBoxToggleBootstrapFormComponent("checkboxToggle");
        checkboxToggle.required();
        editForm.add(checkboxToggle);

        DateFieldBootstrapFormComponent date = new DateFieldBootstrapFormComponent("date");
        date.required();
        editForm.add(date);

        DateTimeFieldBootstrapFormComponent dateTime = new DateTimeFieldBootstrapFormComponent("dateTime");
        dateTime.required();
        editForm.add(dateTime);

        FileInputBootstrapFormComponent fileInput = new FileInputBootstrapFormComponent("fileInput");
        fileInput.required();
        editForm.add(fileInput);

        Select2ChoiceBootstrapFormComponent<Group> preloadedEntitySelect = new Select2ChoiceBootstrapFormComponent<>(
                "preloadedEntitySelect", new GenericChoiceProvider<>(groupRepository.findAll()));
        preloadedEntitySelect.required();
        editForm.add(preloadedEntitySelect);
    }

}
