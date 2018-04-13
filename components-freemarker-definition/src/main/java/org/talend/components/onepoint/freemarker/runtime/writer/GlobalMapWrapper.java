package org.talend.components.onepoint.freemarker.runtime.writer;

import org.talend.components.api.container.RuntimeContainer;

import java.util.AbstractMap;
import java.util.Set;

public class GlobalMapWrapper extends AbstractMap<String, Object> {

    private final RuntimeContainer runtimeContainer;

    public GlobalMapWrapper(RuntimeContainer runtimeContainer) {
        this.runtimeContainer = runtimeContainer;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return null;
    }

    @Override
    public Object get(Object key) {
        return runtimeContainer.getGlobalData(key.toString());
    }
}
