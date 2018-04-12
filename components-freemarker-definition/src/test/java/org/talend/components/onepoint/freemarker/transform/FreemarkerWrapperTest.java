package org.talend.components.onepoint.freemarker.transform;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FreemarkerWrapperTest {

    private FreemarkerWrapper freemarkerWrapper;

    private static File templateFile;

    @Before
    public void setup() throws URISyntaxException {
        loadTemplate("ftl/customerSimple.ftl");
    }

    private void loadTemplate(String template) throws URISyntaxException {
        URL templateLocation = Thread.currentThread().getContextClassLoader().getResource(template);
        if(templateLocation == null) {
            fail("Template could not be found.");
        }
        templateFile = new File(templateLocation.toURI());
        freemarkerWrapper = new FreemarkerWrapper(new FreemarkerConfiguration.Builder(templateFile)
                .setEncoding("UTF-8")
                .build());
    }

    @Test
    public void render() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("givenName", "Gil");
        m.put("familyName", "Fernandes");
        m.put("birthDate", new Date());
        m.put("test-dash", "foo");
        StringWriter w = new StringWriter();
        freemarkerWrapper.render(m, w);
        assertThat(w.toString().contains("Gil"), is(true));
        assertThat(w.toString().contains("Fernandes"), is(true));
        assertThat(w.toString().contains("foo"), is(true));
    }

    @Test (expected = freemarker.core.ParseException.class)
    public void checkFileValidity() throws IOException, URISyntaxException {
        loadTemplate("ftl/rogue.ftl");
        freemarkerWrapper.checkTemplateFile(templateFile);
    }

}