package org.talend.components.onepoint.freemarker.transform;

import java.io.File;
import java.util.Locale;

public class FreemarkerConfiguration {

    private final File templateFile;

    private final String formatNumber;

    private final Locale locale;

    private final String encoding;

    FreemarkerConfiguration(Builder builder) {
        this.templateFile = builder.templateFile;
        this.formatNumber = builder.formatNumber;
        this.locale = builder.locale;
        this.encoding = builder.encoding;
    }

    File getTemplateFile() {
        return templateFile;
    }

    String getFormatNumber() {
        return formatNumber;
    }

    Locale getLocale() {
        return locale;
    }

    String getEncoding() {
        return encoding;
    }

    static class Builder {

        private final File templateFile;

        private Locale locale = Locale.US;

        private String encoding;

        private String formatNumber = "0.######";

        Builder(File templateFile) {
            this.templateFile = templateFile;
        }

        Builder setFormatNumber(String formatNumber) {
            this.formatNumber = formatNumber;
            return this;
        }

        Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        Builder setEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public FreemarkerConfiguration build() {
            return new FreemarkerConfiguration(this);
        }
    }
}
