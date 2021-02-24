/**
 *
 */
package org.devgateway.ocvn.forms.wicket.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import nl.dries.wicket.hibernate.dozer.DozerModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.ValidationError;
import org.devgateway.ocds.persistence.mongo.spring.ExcelImportService;
import org.devgateway.ocvn.forms.wicket.components.LogLabel;
import org.devgateway.ocvn.persistence.dao.VietnamImportSourceFiles;
import org.devgateway.ocvn.persistence.mongo.dao.ImportFileTypes;
import org.devgateway.ocvn.persistence.repository.VietnamImportSourceFilesRepository;
import org.devgateway.toolkit.forms.wicket.components.form.CheckBoxBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.Select2ChoiceBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.Select2MultiChoiceBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.events.EditingDisabledEvent;
import org.devgateway.toolkit.forms.wicket.page.BasePage;
import org.devgateway.toolkit.forms.wicket.page.Homepage;
import org.devgateway.toolkit.forms.wicket.providers.GenericChoiceProvider;
import org.devgateway.toolkit.forms.wicket.providers.GenericPersistableJpaRepositoryTextChoiceProvider;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author mpostelnicu
 *
 */
@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_ADMIN)
@MountPath("/import")
public class VietnamImportPage extends BasePage {

    @SpringBean
    private VietnamImportSourceFilesRepository sourceFilesRepository;

    @SpringBean
    private ExcelImportService vnExcelImportService;

    @SpringBean
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static final long serialVersionUID = 1L;
    private BootstrapForm<VietnamImportBean> importForm;

    private Select2ChoiceBootstrapFormComponent<VietnamImportSourceFiles> sourceFiles;

    private LaddaAjaxButton importButton;

    private LogLabel logText;

    private LaddaAjaxButton doneButton;

    private Select2MultiChoiceBootstrapFormComponent<String> fileTypes;

    private TransparentWebMarkupContainer importContainer;

    private WebMarkupContainer spinner;

    private CheckBoxBootstrapFormComponent dropData;

    private CheckBoxBootstrapFormComponent validateData;

    private CheckBoxBootstrapFormComponent flagData;

    /**
     * @param parameters
     */
    public VietnamImportPage(final PageParameters parameters) {
        super(parameters);
    }

    class VietnamImportBean implements Serializable {
        private static final long serialVersionUID = 1L;

        private VietnamImportSourceFiles sourceFiles;

        private List<String> fileTypes = new ArrayList<>(ImportFileTypes.ALL_FILE_TYPES);

        private Boolean dropData = true;

        private Boolean validateData = true;
        
        private Boolean flagData = true;
        
        public Boolean getValidateData() {
            return validateData;
        }

        public void setValidateData(Boolean validateData) {
            this.validateData = validateData;
        }

        public List<String> getFileTypes() {
            return fileTypes;
        }

        public void setFileTypes(final List<String> fileTypes) {
            this.fileTypes = fileTypes;
        }

        public VietnamImportSourceFiles getSourceFiles() {
            return sourceFiles;
        }

        public void setSourceFiles(final VietnamImportSourceFiles sourceFiles) {
            this.sourceFiles = sourceFiles;
        }

        public Boolean getDropData() {
            return dropData;
        }

        public void setDropData(final Boolean dropData) {
            this.dropData = dropData;
        }

        public Boolean getFlagData() {
            return flagData;
        }

        public void setFlagData(Boolean flagData) {
            this.flagData = flagData;
        }

    }

    protected void addForm() {
        importForm = new BootstrapForm<VietnamImportBean>("form",
                new CompoundPropertyModel<VietnamImportBean>(new DozerModel<>(new VietnamImportBean())));
        importForm.setOutputMarkupId(true);
        add(importForm);
    }

    protected void addSourceFilesSelect() {
        sourceFiles = new Select2ChoiceBootstrapFormComponent<VietnamImportSourceFiles>("sourceFiles",
                new GenericPersistableJpaRepositoryTextChoiceProvider<VietnamImportSourceFiles>(sourceFilesRepository));
        sourceFiles.required();
        importForm.add(sourceFiles);

    }

    protected void addLogText() {

        importContainer = new TransparentWebMarkupContainer("importContainer");
        importContainer.setOutputMarkupId(true);
        importForm.add(importContainer);

        AbstractReadOnlyModel<String> logTextModel = new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return vnExcelImportService.getMsgBuffer().toString();
            }
        };

        logText = new LogLabel("logText", logTextModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onPostProcessTarget(final AjaxRequestTarget target) {
                if (threadPoolTaskExecutor.getActiveCount() == 0) {
                    getSelfUpdatingBehavior().stop(target);
                    spinner.setVisibilityAllowed(false);
                    target.add(spinner);
                }
            }
        };
        importContainer.add(logText);

        spinner = new WebMarkupContainer("spinner");
        spinner.setOutputMarkupId(true);
        importContainer.add(spinner);
    }

    protected void addDropData() {
        dropData = new CheckBoxBootstrapFormComponent("dropData");
        importForm.add(dropData);
    }

    protected void addValidateData() {
        validateData = new CheckBoxBootstrapFormComponent("validateData");
        importForm.add(validateData);
    }
    
    protected void addFlagData() {
        flagData = new CheckBoxBootstrapFormComponent("flagData");
        importForm.add(flagData);
    }


    protected void addFileTypesSelect() {
        fileTypes = new Select2MultiChoiceBootstrapFormComponent<String>("fileTypes",
                new GenericChoiceProvider<String>(ImportFileTypes.ALL_FILE_TYPES) {

                    private static final long serialVersionUID = -2273682733952308598L;

                    @Override
                    public String getDisplayValue(final String arg0) {
                        return arg0;
                    }

                    @Override
                    public String getIdValue(final String arg0) {
                        return arg0;
                    }

                });
       //fileTypes.required();
        importForm.add(fileTypes);
    }

    protected void addDoneButton() {
        doneButton = new LaddaAjaxButton("done", Type.Default) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                setResponsePage(Homepage.class);
            }
        };
        doneButton.setDefaultFormProcessing(false);
        doneButton.setLabel(new ResourceModel("done"));
        doneButton.setDefaultFormProcessing(false);
        doneButton.setIconType(FontAwesomeIconType.thumbs_up);
        importForm.add(doneButton);
    }

    protected void addImportButton() {
        importButton = new LaddaAjaxButton("import", Type.Danger) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                send(getPage(), Broadcast.BREADTH, new EditingDisabledEvent());
                logText.getSelfUpdatingBehavior().restart(target);
                importContainer.setVisibilityAllowed(true);
                target.add(importContainer);
                target.add(form);

                try {

                    vnExcelImportService.importAllSheets(importForm.getModelObject().getFileTypes(),
                            importForm.getModelObject().getSourceFiles().getPrototypeDatabaseFile().isEmpty() ? null
                            : importForm.getModelObject().getSourceFiles().getPrototypeDatabaseFile().iterator()
                                    .next().getContent().getBytes(),
                            importForm.getModelObject().getSourceFiles().getLocationsFile().isEmpty() ? null
                            : importForm.getModelObject().getSourceFiles().getLocationsFile().iterator().next()
                                    .getContent().getBytes(),
                            importForm.getModelObject().getSourceFiles().getPublicInstitutionsSuppliersFile().isEmpty()
                            ? null
                            : importForm.getModelObject().getSourceFiles().getPublicInstitutionsSuppliersFile()
                                    .iterator().next().getContent().getBytes(),
                            importForm.getModelObject().getSourceFiles().getCityDepartmentGroupFile().isEmpty()
                            ? null
                            : importForm.getModelObject().getSourceFiles().getCityDepartmentGroupFile()
                                    .iterator().next().getContent().getBytes(),
                            importForm.getModelObject().getDropData(), importForm.getModelObject().getValidateData(),
                            importForm.getModelObject().getFlagData());
                } catch (Exception e) {
                    logger.error(e);
                    e.printStackTrace();
                } finally {
                    target.add(logText);
                    target.add(feedbackPanel);
                    this.setEnabled(false);
                    target.add(this);
                }

            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                ValidationError error = new ValidationError();
                error.addKey("formHasErrors");
                error(error);

                target.add(form);
                target.add(feedbackPanel);
            }
        };
        importButton.setLabel(new ResourceModel("startImportProcess"));
        importButton.setIconType(FontAwesomeIconType.hourglass_start);
        importForm.add(importButton);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        vnExcelImportService.newMsgBuffer();

        addForm();
        addSourceFilesSelect();
        addFileTypesSelect();
        addImportButton();
        addLogText();
        addDoneButton();
        addDropData();
        addValidateData();
        addFlagData();

        switchFieldsBasedOnExecutorAvailability(null);

    }

    private void switchFieldsBasedOnExecutorAvailability(final AjaxRequestTarget target) {
        boolean enabled = threadPoolTaskExecutor.getActiveCount() == 0;

        importContainer.setVisibilityAllowed(!enabled);
        sourceFiles.setEnabled(enabled);
        fileTypes.setEnabled(enabled);
        importButton.setEnabled(enabled);

        if (target != null) {
            target.add(sourceFiles);
            target.add(fileTypes);
            target.add(importButton);
        }

    }

}
