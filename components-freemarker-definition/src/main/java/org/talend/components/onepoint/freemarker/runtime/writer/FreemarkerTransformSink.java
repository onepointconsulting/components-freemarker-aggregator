package org.talend.components.onepoint.freemarker.runtime.writer;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.onepoint.freemarker.transform.FreemarkerTransformProperties;
import org.talend.components.onepoint.freemarker.transform.TemplateValidator;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FreemarkerTransformSink implements Sink {

    private Schema schemaMain;

    private Schema schemaFlow;

    private Schema schemaReject;

    private FreemarkerTransformProperties properties;

    @Override
    public WriteOperation<?> createWriteOperation() {
        return new FreemarkerTransformRowWriteOperation(this, schemaMain, schemaFlow, schemaReject);
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer runtimeContainer) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer runtimeContainer, String s) throws IOException {
        return null;
    }

    @Override
    public ValidationResult validate(RuntimeContainer runtimeContainer) {
        return TemplateValidator.validate(properties);
    }

    @Override
    public ValidationResult initialize(RuntimeContainer runtimeContainer, ComponentProperties componentProperties) {
        final FreemarkerTransformProperties properties = (FreemarkerTransformProperties) componentProperties;
        this.properties = properties;
        this.schemaMain = properties.schemaMain.schema.getValue();
        this.schemaFlow = properties.schemaFlow.schema.getValue();
        this.schemaReject = properties.schemaReject.schema.getValue();
        return ValidationResult.OK;
    }

    public FreemarkerTransformProperties getProperties() {
        return properties;
    }
}
