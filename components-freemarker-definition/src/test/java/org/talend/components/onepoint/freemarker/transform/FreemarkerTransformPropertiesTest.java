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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.talend.components.onepoint.freemarker.CharSets;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class FreemarkerTransformPropertiesTest {

	/**
	 * Checks forms are filled with required widgets
	 */
	@Test
	public void testSetupLayout() {
		FreemarkerTransformProperties properties = new FreemarkerTransformProperties("root");
		properties.schemaMain.init();

		properties.setupLayout();

		Form main = properties.getForm(Form.MAIN);
		assertThat(main, notNullValue());

		Collection<Widget> mainWidgets = main.getWidgets();
		assertThat(mainWidgets, hasSize(10));

		Widget schemaWidget = main.getWidget("schemaMain");
		assertThat(schemaWidget, notNullValue());

		Widget fileWidget = main.getWidget("filename");
		assertThat(fileWidget, notNullValue());

		Widget useCustomDelimiterWidget = main.getWidget("useCustomCharset");
		assertThat(useCustomDelimiterWidget, notNullValue());

		Widget delimiterWidget = main.getWidget("charset");
		assertThat(delimiterWidget, notNullValue());

		Widget customDelimiterWidget = main.getWidget("useCustomNumberFormat");
		assertThat(customDelimiterWidget, notNullValue());

		Widget guessSchemaWidget = main.getWidget("validateTemplate");
		assertThat(guessSchemaWidget, notNullValue());
	}

	/**
	 * Checks default values are set correctly
	 */
	@Test
	public void testSetupProperties() {
		FreemarkerTransformProperties properties = new FreemarkerTransformProperties("root");
		properties.setupProperties();

		CharSets delimiter = properties.charset.getValue();
		assertThat(delimiter, equalTo(CharSets.UTF_8));

		boolean useCustomCharset = properties.useCustomCharset.getValue();
		assertEquals(false, useCustomCharset);

		String customDelimiter = properties.customCharset.getValue();
		assertThat(customDelimiter, equalTo(""));
	}

	/**
	 * Checks initial layout
	 */
	@Test
	public void testRefreshLayout() {
		FreemarkerTransformProperties properties = new FreemarkerTransformProperties("root");
		properties.init();

		properties.refreshLayout(properties.getForm(Form.MAIN));

		boolean schemaHidden = properties.getForm(Form.MAIN).getWidget("schemaMain").isHidden();
		assertFalse(schemaHidden);

		boolean filenameHidden = properties.getForm(Form.MAIN).getWidget("filename").isHidden();
		assertFalse(filenameHidden);

		boolean useCustomDelimiterHidden = properties.getForm(Form.MAIN).getWidget("validateTemplate").isHidden();
		assertFalse(useCustomDelimiterHidden);

		boolean delimiterHidden = properties.getForm(Form.MAIN).getWidget("useCustomCharset").isHidden();
		assertFalse(delimiterHidden);

		boolean customDelimiterHidden = properties.getForm(Form.MAIN).getWidget("useCustomNumberFormat").isHidden();
		assertFalse(customDelimiterHidden);

		boolean guessSchemaHidden = properties.getForm(Form.MAIN).getWidget("locale").isHidden();
		assertFalse(guessSchemaHidden);
	}
}
