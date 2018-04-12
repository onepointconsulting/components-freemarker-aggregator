package org.talend.components.onepoint.freemarker.runtime.writer;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

import java.util.Map;

public class FreemarkerTransformRowWriteOperation implements WriteOperation<Result> {

    private final FreemarkerTransformSink sink;

    private final Schema schemaMain;

    private final Schema schemaFlow;

    private final Schema schemaReject;

    public FreemarkerTransformRowWriteOperation(FreemarkerTransformSink freemarkerTransformSink,
                                                Schema schemaMain, Schema schemaFlow, Schema schemaReject) {
        this.sink = freemarkerTransformSink;
        this.schemaMain = schemaMain;
        this.schemaFlow = schemaFlow;
        this.schemaReject = schemaReject;
    }

    @Override
    public void initialize(RuntimeContainer runtimeContainer) {

    }

    @Override
    public Map<String, Object> finalize(Iterable<Result> iterable, RuntimeContainer runtimeContainer) {
        return Result.accumulateAndReturnMap(iterable);
    }

    @Override
    public Writer<Result> createWriter(RuntimeContainer runtimeContainer) {
        return new FreemarkerTransformWriter(this, runtimeContainer);
    }

    @Override
    public FreemarkerTransformSink getSink() {
        return sink;
    }

    Schema getSchemaMain() {
        return schemaMain;
    }

    Schema getSchemaFlow() {
        return schemaFlow;
    }

    Schema getSchemaReject() {
        return schemaReject;
    }
}
