package org.talend.components.onepoint.freemarker.transform;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used to validate the Freemarker template.
 */
public class TemplateValidator {

    public static ValidationResult validate(FreemarkerTransformProperties properties) {
        final String fileName = properties.filename.getValue();
        Path filePath = Paths.get(fileName);
        if(StringUtils.isEmpty(fileName)) {
            return new ValidationResult(ValidationResult.Result.ERROR, properties.getI18nMessage("error.templateIsEmpty"));
        }
        if(!Files.exists(filePath)) {
            return new ValidationResult(ValidationResult.Result.ERROR, properties.getI18nMessage("error.templateDoesNotExist", filePath));
        }
        FreemarkerWrapper freemarkerWrapper = new FreemarkerWrapper(FreemarkerConfigurationFactory.createFrom(properties));
        try {
            freemarkerWrapper.checkCurrentFile();
            return new ValidationResult(ValidationResult.Result.OK, properties.getI18nMessage("success.fileReading",
                    fileName));
        } catch (IOException e) {
            return new ValidationResult(ValidationResult.Result.ERROR, properties.getI18nMessage("error.fileReading",
                    e.getMessage()));
        }
    }
}
