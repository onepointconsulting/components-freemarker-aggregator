package org.talend.components.onepoint.freemarker.runtime.writer;

import freemarker.template.TemplateException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.WriterWithFeedback;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.onepoint.freemarker.transform.FreemarkerConfiguration;
import org.talend.components.onepoint.freemarker.transform.FreemarkerConfigurationFactory;
import org.talend.components.onepoint.freemarker.transform.FreemarkerTransformProperties;
import org.talend.components.onepoint.freemarker.transform.FreemarkerWrapper;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.IndexedRecordConverter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FreemarkerTransformWriter implements WriterWithFeedback<Result, IndexedRecord, IndexedRecord> {

    private transient static final Logger LOG = LoggerFactory.getLogger(FreemarkerTransformWriter.class);

    private final FreemarkerTransformRowWriteOperation writeOperation;

    private final RuntimeContainer container;

    private List<IndexedRecord> success;

    private String uid;

    private List<IndexedRecord> reject;

    private int successCount;

    private int rejectCount;

    private int totalCount;

    private FreemarkerConfiguration freemarkerConfiguration;

    private FreemarkerWrapper freemarkerWrapper;

    private IndexedRecordConverter<Object, ? extends IndexedRecord> factory;

    private transient Schema schemaMain;

    private transient Schema schemaFlow;

    private transient Schema schemaReject;

    private final FreemarkerTransformProperties properties;
    private GlobalMapWrapper globalMapWrapper;

    public FreemarkerTransformWriter(FreemarkerTransformRowWriteOperation writeOperation, RuntimeContainer runtimeContainer) {
        this.writeOperation = writeOperation;
        this.container = runtimeContainer;
        this.freemarkerConfiguration = FreemarkerConfigurationFactory.createFromOperation(writeOperation);
        freemarkerWrapper = new FreemarkerWrapper(freemarkerConfiguration);
        properties = writeOperation.getSink().getProperties();
    }

    @Override
    public Iterable<IndexedRecord> getSuccessfulWrites() {
        List<IndexedRecord> successResult = new LinkedList<>(success);
        success.clear();
        return successResult;
    }

    @Override
    public Iterable<IndexedRecord> getRejectedWrites() {
        List<IndexedRecord> rejectsResult = new LinkedList<>(reject);
        reject.clear();
        return rejectsResult;
    }

    @Override
    public void open(String s) throws IOException {
        this.uid = s;
        this.success = new LinkedList<>();
        this.reject = new LinkedList<>();
        if (schemaMain == null) {
            schemaMain = this.writeOperation.getSchemaMain();
        }
        if (schemaFlow == null) {
            schemaFlow = this.writeOperation.getSchemaFlow();
        }
        if (schemaReject == null) {
            schemaReject = this.writeOperation.getSchemaReject();
        }
    }

    @Override
    public void write(Object datum) throws IOException {
        if (datum == null) {
            return;
        } // else handle the data.
        totalCount++;
        IndexedRecord inputRecord = getFactory(datum).convertToAvro((IndexedRecord) datum);
        processRecord(inputRecord);
    }

    @SuppressWarnings("unchecked")
    private IndexedRecordConverter<Object, ? extends IndexedRecord> getFactory(Object datum) {
        if (factory == null) {
            factory = (IndexedRecordConverter<Object, ? extends IndexedRecord>) new AvroRegistry()
                    .createIndexedRecordConverter(datum.getClass());
        }
        return factory;
    }

    private void processRecord(IndexedRecord input) {
        IndexedRecord successful = new GenericData.Record(schemaFlow);
        Map<String, Object> freemarkerData = copyToMap(input);
        FreemarkerError processingSuccess = processFreemarkerField(successful, freemarkerData);
        if (processingSuccess.isSuccess()) {
            if (hasOutputWith(schemaFlow)) {
                copyToOutput(input, successful, schemaFlow, success);
            }
            successCount++;
        } else {
            if (hasOutputWith(schemaReject)) {
                IndexedRecord rejected = new GenericData.Record(schemaReject);
                processErrorField(rejected, processingSuccess);
                copyToOutput(input, rejected, schemaReject, reject);
            }
            rejectCount++;
        }
    }

    private Map<String, Object> copyToMap(IndexedRecord input) {
        Map<String, Object> freemarkerData = new HashMap<>();
        if(properties.injectGlobalMap.getValue()) {
            globalMapWrapper = new GlobalMapWrapper(container);
            freemarkerData.put("globalMap", globalMapWrapper);
        }
        for (Schema.Field inField : schemaMain.getFields()) {
            String inName = inField.name();
            Object outValue = input.get(inField.pos());
            final boolean isNull = outValue == null;
            if (!isNull) {
                // Add to Freemarker map
                freemarkerData.put(inName, outValue);
            }
        }
        return freemarkerData;
    }

    private FreemarkerError processFreemarkerField(IndexedRecord successful, Map<String, Object> freemarkerData) {
        Schema.Field freemarkerOutputField = schemaFlow.getField(FreemarkerTransformProperties.FIELD_FREEMARKER_OUTPUT);
        StringWriter stringWriter = new StringWriter();
        try {
            freemarkerWrapper.render(freemarkerData, stringWriter);
            successful.put(freemarkerOutputField.pos(), stringWriter.toString());
            return new FreemarkerError(true);
        } catch (IOException | TemplateException e) {
            LOG.warn("Could not process data: {}.", freemarkerData, e);
            return new FreemarkerError(false, e.getMessage(), e);
        }
    }

    private boolean hasOutputWith(Schema schema) {
        return schema != null && !schema.getFields().isEmpty();
    }

    private void copyToOutput(IndexedRecord input, IndexedRecord record, Schema outSchema, List<IndexedRecord> targetList) {
        for (Schema.Field inField : schemaMain.getFields()) {
            String inName = inField.name();
            Object outValue = input.get(inField.pos());
            final boolean isNull = outValue == null;
            if (!isNull) {
                Schema.Field outField = outSchema.getField(inName);
                if (outField != null) {
                    // Copy to output
                    record.put(outField.pos(), outValue);
                }
            }
        }
        targetList.add(record);
    }

    private void processErrorField(IndexedRecord rejected, FreemarkerError processingSuccess) {
        Schema.Field errorField = schemaReject.getField(FreemarkerTransformProperties.FIELD_ERROR_MESSAGE);
        rejected.put(errorField.pos(), processingSuccess.getErrorMessage());
    }

    @Override
    public Result close() throws IOException {
        return new Result(uid, totalCount, successCount, rejectCount);
    }

    @Override
    public WriteOperation<Result> getWriteOperation() {
        return writeOperation;
    }
}
