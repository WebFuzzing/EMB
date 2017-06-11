package org.javiermf.features.exceptions;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class WrongProductConfigurationException extends Exception {
    public WrongProductConfigurationException(Set<String> evaluationErrorMessages) {
        super("Wrong product configuration: " + StringUtils.join(evaluationErrorMessages, ", "));
    }
}
