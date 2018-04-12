package org.talend.components.onepoint.freemarker.transform;

import org.apache.commons.lang3.LocaleUtils;
import org.talend.components.onepoint.freemarker.NamedEnum;
import org.talend.components.onepoint.freemarker.runtime.writer.FreemarkerTransformRowWriteOperation;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

import java.io.File;
import java.util.Locale;

/**
 * Used to convert the Talend properties to a {@link FreemarkerConfiguration} object.
 */
public class FreemarkerConfigurationFactory {

    public static FreemarkerConfiguration createFromOperation(FreemarkerTransformRowWriteOperation writeOperation) {
        FreemarkerTransformProperties properties = writeOperation.getSink().getProperties();
        return createFrom(properties);
    }

    static FreemarkerConfiguration createFrom(FreemarkerTransformProperties properties) {
        String filename = properties.filename.getValue();
        File file = new File(filename);
        String encoding = extractFromProperties(properties.useCustomCharset, properties.customCharset, properties.charset);
        String numberFormat = extractFromProperties(properties.useCustomNumberFormat, properties.customNumberFormat, properties.numberFormat);
        Locale locale = LocaleUtils.toLocale(properties.locale.getValue().toString());
        return new FreemarkerConfiguration.Builder(file).setEncoding(encoding)
                .setFormatNumber(numberFormat).setLocale(locale).build();
    }

    private static String extractFromProperties(Property<Boolean> booleanProperty, Property<String> stringPproperty,
                                                EnumProperty<? extends NamedEnum> enumProperty) {
        if (booleanProperty.getValue()) {
            return stringPproperty.getValue();
        }
        return enumProperty.getValue().getName();
    }
}
