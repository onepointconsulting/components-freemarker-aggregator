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

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.onepoint.freemarker.CharSets;
import org.talend.components.onepoint.freemarker.Locales;
import org.talend.components.onepoint.freemarker.NumberFormats;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The ComponentProperties subclass provided by a component stores the
 * configuration of a component and is used for:
 * <ol>
 * <li>Specifying the format and type of information (properties) that is
 * provided at design-time to configure a component for run-time,</li>
 * <li>Validating the properties of the component at design-time,</li>
 * <li>Containing all of the UI information for laying out and presenting the
 * properties to the user.</li>
 * </ol>
 * <p>
 * The FreemarkerTransformProperties has following properties:
 * <ol>
 * <li>{@code filename}, a simple property which is a String containing the
 * file path that this component will read.</li>
 * <li>{@code schema}, an embedded property referring to a Schema.</li>
 * <li>{@code delimiter}, a string property containing field delimiter,
 * which is used in a file that this component will read.</li>
 * </ol>
 */
public class FreemarkerTransformProperties extends FixedConnectorsComponentProperties {

    public static final String FIELD_FREEMARKER_OUTPUT = "freemarkerOutput";
    public static final String FIELD_ERROR_MESSAGE = "errorMessage";

    /**
     * Out of band (a.k.a flow variables) data schema
     * <p>
     * It has one field: int currentLine
     */
    public static final Schema outOfBandSchema;

    private static final CharSets DEFAULT_CHARSET = CharSets.UTF_8;

    /**
     * Sets Out of band schema. This schema is not supposed to be changed by user
     */
    static {
        Field currentLineField = new Field("CURRENT_LINE", Schema.create(Schema.Type.INT), null, (Object) null);
        outOfBandSchema = Schema.createRecord("OutOfBand", null, null, false);
        outOfBandSchema.setFields(Collections.singletonList(currentLineField));
    }

    /**
     * Stores path to file to be read <br>
     * Note: property <code>name</code>, which is
     * passed to factory should be exactly the same as Property field name Here,
     * field name is filename and property name is "filename"
     * <p>
     * Specify i18n messages for all {@link Property} defined in this class in
     * FreemarkerTransformProperties.properties file
     */
    public final Property<String> filename = PropertyFactory.newString("filename").setRequired(true); //$NON-NLS-1$

    /**
     * Design schema of input component. Design schema defines data fields which
     * should be retrieved from Data Store. In this component example Data Store
     * is a single file on file system
     */
    public final SchemaProperties schemaFlow = new SchemaProperties("schemaFlow"); //$NON-NLS-1$

    public final SchemaProperties schemaReject = new SchemaProperties("schemaReject");

    /**
     * Stores chosen delimiter. Property of type {@link EnumProperty} will be
     * shown as dropdown list in UI
     */
    public final EnumProperty<CharSets> charset = new EnumProperty<>(CharSets.class, "charset"); //$NON-NLS-1$

    /**
     * Property parameterized with Boolean will be shown as a checkbox in UI If
     * this property is true it allows user to specify custom delimiter
     */
    public final Property<Boolean> useCustomCharset = PropertyFactory.newBoolean("useCustomCharset");

    public final Property<String> customCharset = PropertyFactory.newString("customCharset");

    public final Property<Boolean> useCustomNumberFormat = PropertyFactory.newBoolean("useCustomNumberFormat");

    public final EnumProperty<NumberFormats> numberFormat = new EnumProperty<>(NumberFormats.class, "numberFormat");

    public final Property<String> customNumberFormat = PropertyFactory.newString("customNumberFormat");

    public final EnumProperty<Locales> locale = new EnumProperty<>(Locales.class, "locale"); //$NON-NLS-1$

    public final transient PresentationItem validateTemplate = new PresentationItem("validateTemplate", "Validate template");

    public final Property<Boolean> injectGlobalMap = PropertyFactory.newBoolean("injectGlobalMap");

    public ISchemaListener schemaListener;

    public final SchemaProperties schemaMain = new SchemaProperties("schemaMain") {
        public void afterSchema() {
            schemaListener.afterSchema();
        }
    };

    /**
     * This field specifies path {@link SchemaProperties} associated with some
     * connector. This is used to retrieve schema value from
     * {@link FixedConnectorsComponentProperties} class
     */
    private transient PropertyPathConnector flowConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schemaFlow"); //$NON-NLS-1$
    private transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schemaMain");
    private transient PropertyPathConnector rejectConnector = new PropertyPathConnector(Connector.REJECT_NAME, "schemaReject");

    public FreemarkerTransformProperties(String name) {
        super(name);
        setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                updateOutputSchemas();
                updateRejectSchemas();
            }
        });
    }

    public void setSchemaListener(ISchemaListener schemaListener) {
        this.schemaListener = schemaListener;
    }

    private void updateOutputSchemas() {
        Schema schema = schemaMain.schema.getValue();

        final List<Field> additionalFlowFields = new ArrayList<>();
        addTextField(additionalFlowFields, FIELD_FREEMARKER_OUTPUT);

        Schema flowOutputSchema = newSchema(schema, "flowOutput", additionalFlowFields);
        schemaFlow.schema.setValue(flowOutputSchema);
    }

    private void updateRejectSchemas() {
        Schema schema = schemaMain.schema.getValue();
        final List<Field> additionalFlowFields = new ArrayList<>();

        addTextField(additionalFlowFields, FIELD_ERROR_MESSAGE);
        Schema rejectSchema = newSchema(schema, "rejectOutput", additionalFlowFields);
        schemaReject.schema.setValue(rejectSchema);
    }

    private void addTextField(List<Field> additionalFlowFields, String fieldName) {
        Field field = new Field(fieldName, Schema.create(Schema.Type.STRING), null, (Object) null);
        field.addProp(SchemaConstants.TALEND_IS_LOCKED, "false");
        field.addProp(SchemaConstants.TALEND_FIELD_GENERATED, "true");
        field.addProp(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "4000"); // 4000 for Oracle
        additionalFlowFields.add(field);
    }

    // Copied from tFilterRowProperties
    private Schema newSchema(Schema metadataSchema, String newSchemaName, List<Schema.Field> moreFields) {
        Schema newSchema = Schema.createRecord(newSchemaName, metadataSchema.getDoc(), metadataSchema.getNamespace(),
                metadataSchema.isError());
        // TODO duplicate with salesforce, make it to a common one?
        List<Schema.Field> copyFieldList = new ArrayList<>();
        for (Schema.Field se : metadataSchema.getFields()) {
            Schema.Field field = new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal(), se.order());
            field.getObjectProps().putAll(se.getObjectProps());
            copyFieldList.add(field);
        }

        copyFieldList.addAll(moreFields);

        copyFieldList = removeDuplicates(copyFieldList);

        newSchema.setFields(copyFieldList);
        for (Map.Entry<String, Object> entry : metadataSchema.getObjectProps().entrySet()) {
            newSchema.addProp(entry.getKey(), entry.getValue());
        }

        return newSchema;
    }

    private List<Field> removeDuplicates(List<Field> copyFieldList) {
        Map<String, List<Field>> map = copyFieldList.stream().collect(Collectors.groupingBy(Field::name));
        return map.values().stream().map(l -> l.get(0)).collect(Collectors.toList());
    }

    /**
     * Default properties values are set in this method
     * <p>
     * Note: first line in this method should be
     * <code>super.setupProperties();</code>
     */
    @Override
    public void setupProperties() {
        super.setupProperties();
        charset.setValue(DEFAULT_CHARSET);
        useCustomCharset.setValue(false);
        customCharset.setValue("");
        customNumberFormat.setValue("0.######");
        useCustomNumberFormat.setValue(false);
        numberFormat.setValue(NumberFormats.COMPUTER);
        locale.setValue(Locales.en);
        injectGlobalMap.setValue(false);
    }

    /**
     * Sets UI elements layout on the form {@link Form#addRow(Widget)} sets new
     * element under previous one {@link Form#addColumn(Widget)} sets new element to
     * the right of previous one in the same row
     * <p>
     * Note: first line in this method should be
     * <code>super.setupLayout();</code>
     */
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(schemaMain.getForm(Form.REFERENCE));
        form.addRow(Widget.widget(filename).setWidgetType(Widget.FILE_WIDGET_TYPE));
        form.addRow(Widget.widget(validateTemplate).setWidgetType(Widget.BUTTON_WIDGET_TYPE));
        form.addRow(useCustomCharset);
        form.addColumn(charset);
        form.addColumn(customCharset);
        form.addRow(useCustomNumberFormat);
        form.addColumn(numberFormat);
        form.addColumn(customNumberFormat);
        form.addRow(locale);

        Form advanced = Form.create(this, Form.ADVANCED);
        advanced.addRow(injectGlobalMap);
    }

    /**
     * Refreshes <code>form</code> layout after some changes. Often it is used
     * to show or hide some UI elements
     * <p>
     * Note: first line in this method should be
     * <code>super.refreshLayout(form);</code>
     */
    @Override
    public void refreshLayout(final Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            conditionalField(useCustomCharset, charset, customCharset, form);
            conditionalField(useCustomNumberFormat, numberFormat, customNumberFormat, form);
        }
    }

    /**
     * Callback method. Runtime Platform calls it after changes with UI element
     * This method should have name if following format {@code after
     * <PropertyName>}
     */
    public void afterUseCustomCharset() {
        refreshLayout(getForm(Form.MAIN));
    }

    private void conditionalField(Property<Boolean> booleanProperty, EnumProperty<?> enumProperty,
                                  Property<String> property, final Form form) {
        if (booleanProperty.getValue()) {
            form.getWidget(enumProperty.getName()).setHidden();
            form.getWidget(property.getName()).setVisible();
        } else {
            form.getWidget(enumProperty.getName()).setVisible();
            form.getWidget(property.getName()).setHidden();
        }
    }

    public void afterUseCustomNumberFormat() {
        refreshLayout(getForm(Form.MAIN));
    }

    /**
     * This is a callback, which is called, when user presses "Guess Schema" button.
     * It tries to read data sample and guess schema using specified properties.
     * If properties are not correctly set or error occurs during reading then
     * error {@link ValidationResult} is returned.
     * If everything is ok gueessed schema is set as value of SchemaProperties.
     *
     * @return {@link ValidationResult}
     */
    public ValidationResult validateValidateTemplate() {
        return TemplateValidator.validate(this);
    }

    /**
     * Refreshes form after "Guess Schema" button was processed
     */
    public void afterValidateTemplate() {
        refreshLayout(getForm(Form.MAIN));
    }

    /**
     * Returns input or output component connectors
     *
     * @param isOutputConnectors specifies what connectors to return, true if output connectors
     *                           are requires, false if input connectors are requires
     * @return component connectors
     */
    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnectors) {
        Set<PropertyPathConnector> connectors = new HashSet<>();
        if (isOutputConnectors) {
            connectors.add(flowConnector);
            connectors.add(rejectConnector);
        } else {
            connectors.add(mainConnector);
        }
        return connectors;
    }
}
