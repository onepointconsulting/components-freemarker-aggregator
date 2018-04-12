// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.onepoint.freemarker.transform;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.AbstractComponentDefinition;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.component.runtime.SimpleRuntimeInfo;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.onepoint.freemarker.RuntimeInfoProvider;
import org.talend.components.onepoint.freemarker.runtime.writer.FreemarkerTransformSink;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * The FreemarkerTransformDefinition acts as an entry point for all of services that 
 * a component provides to integrate with the Runtime Platform (at design-time) and other 
 * components (at run-time).
 */
public class FreemarkerTransformDefinition extends AbstractComponentDefinition {
    
    public static final String COMPONENT_NAME = "FreemarkerTransform"; //$NON-NLS-1$

    public FreemarkerTransformDefinition() {
        super(COMPONENT_NAME, ExecutionEngine.DI);
    }

    @Override
    public String[] getFamilies() {
        return new String[] { "Processing" }; //$NON-NLS-1$
    }

    /**
     * Defines a list of Return Properties (a.k.a After Properties). 
     * These properties collect different metrics and information during component execution.
     * Values of these properties are returned after component finished his work.
     * Runtime Platform may use this method to retrieve a this list and show in UI
     * Here, it is defined 2 properties: <br>
     * 1) Error message
     * 2) Number of records processed
     * For Error message property no efforts are required from component developer to set its value. 
     * Runtime Platform will set its value by itself in case of Exception in runtime.
     * As for Number of records property see Reader implementation in runtime part
     */
    @Override
    public Property[] getReturnProperties() {
        return new Property[] { RETURN_TOTAL_RECORD_COUNT_PROP, RETURN_SUCCESS_RECORD_COUNT_PROP,
                RETURN_ERROR_MESSAGE_PROP, RETURN_REJECT_RECORD_COUNT_PROP };
    }
    
    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return FreemarkerTransformProperties.class;
    }
    
    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
                                      ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        if (connectorTopology == ConnectorTopology.INCOMING_AND_OUTGOING) {
            return new SimpleRuntimeInfo(this.getClass().getClassLoader(),
                    DependenciesReader.computeDependenciesFilePath(RuntimeInfoProvider.MAVEN_GROUP_ID,
                            RuntimeInfoProvider.MAVEN_RUNTIME_ARTIFACT_ID),
                    FreemarkerTransformSink.class.getCanonicalName());
        } else {
            return null;
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.INCOMING_AND_OUTGOING);
    }

    @Override
    public boolean isSchemaAutoPropagate() {
        return true;
    }

    @Override
    public boolean isConditionalInputs() {
        return true;
    }

}
