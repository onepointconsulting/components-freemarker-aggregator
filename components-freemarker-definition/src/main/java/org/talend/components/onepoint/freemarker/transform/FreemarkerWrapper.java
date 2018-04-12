package org.talend.components.onepoint.freemarker.transform;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Wrapper class around the Freemarker configuration class.
 */
public class FreemarkerWrapper {

    private final FreemarkerConfiguration freemarkerConfiguration;

    private Configuration configuration;

    public FreemarkerWrapper(FreemarkerConfiguration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
        initConfig(freemarkerConfiguration);
    }

    private void initConfig(FreemarkerConfiguration freemarkerConfiguration) {
        File templateFile = freemarkerConfiguration.getTemplateFile();
        String numberFormat = freemarkerConfiguration.getFormatNumber();
        if(templateFile == null) {
            throw new IllegalArgumentException("The template file is null.");
        }
        if(!templateFile.exists()) {
            throw new IllegalArgumentException("The file does not exist.");
        }
        TemplateLoader ftl = null;
        try {
            ftl = new FileTemplateLoader(templateFile.getParentFile());
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not find template file: %s.", templateFile));
        }
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setNumberFormat(numberFormat);
        configuration.setTemplateLoader(ftl);
        configuration.setEncoding(freemarkerConfiguration.getLocale(), freemarkerConfiguration.getEncoding());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void render(Map<String, Object> data, Writer writer) throws IOException, TemplateException {
        Template template = configuration.getTemplate(freemarkerConfiguration.getTemplateFile().getName(), freemarkerConfiguration.getLocale());
        template.process(data, writer);
    }

    void checkCurrentFile() throws IOException {
        checkTemplateFile(freemarkerConfiguration.getTemplateFile());
    }

    void checkTemplateFile(File file) throws IOException {
        String name = file.getName();
        BufferedReader reader = Files.newBufferedReader(Paths.get(file.toURI()), Charset.forName("UTF-8"));
        checkTemplate(name, reader);
    }

    private void checkTemplate(String name, Reader reader) throws IOException {
        new Template(name, reader, configuration);
    }

}
