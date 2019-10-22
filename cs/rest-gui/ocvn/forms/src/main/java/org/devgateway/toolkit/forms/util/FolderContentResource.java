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
package org.devgateway.toolkit.forms.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;

/**
 * This is a simple resource that allows us to one file within a folder, the
 * file name is given as URL parameter
 * 
 * WARNING, these files are UNPROTECTED, i usually use this to share folders
 * that contain only shareable items (like temporary export folders) DO NOTSHARE
 * SYSTEM FOLDERS
 * 
 * @see AbstractReportPage
 * @author mpostelnicu
 * 
 *         Adapted after http://stackoverflow.com/a/9232848
 */
public class FolderContentResource implements IResource {
    /**
     * 
     */

    public static final String PARAM_FILE_NAME = "fileName";

    private static final long serialVersionUID = 1L;
    private final File rootFolder;

    public FolderContentResource(final File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public void respond(final Attributes attributes) {
        PageParameters parameters = attributes.getParameters();
        String fileName = parameters.get(PARAM_FILE_NAME).toString();

        // we use FilenameUtils to prevent "security tricks", only a file name
        // without path is allowed
        File file = new File(rootFolder, FilenameUtils.getName(fileName));
        FileResourceStream fileResourceStream = new FileResourceStream(file);
        ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
        resource.respond(attributes);
    }
}