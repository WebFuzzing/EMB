/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.config;

import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author jose
 */
@Configuration
public class DocumentsConfig {
    
    @Value("${documents.path}")
    private String filesPath;
    
    @Bean(name = "documentsPath")
    public String configDocumentsPath() throws IOException{
        if(Document.DIRECTORY_PATH==null) Document.DIRECTORY_PATH = this.filesPath;
        File file = new File(Document.DIRECTORY_PATH);
        FileUtils.forceMkdir(file);
        return Document.DIRECTORY_PATH;
    }
}
