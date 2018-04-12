// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%${symbol_escape}features${symbol_escape}org.talend.rcp.branding.%PRODUCTNAME%${symbol_escape}%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.onepoint.freemarker;

/**
 * Enumeration of standard string delimiters
 * 
 * This shows possible values for dropdown list UI element i18n message for it
 * should be placed in TableInputProperties.properties file
 */
public enum CharSets implements NamedEnum {
    US_ASCII("US-ASCII"),
    ISO_8859_1("ISO-8859-1"),
    UTF_8("UTF-8"),
    UTF_16BE("UTF-16BE"),
    UTF_16LE("UTF-16LE"),
    UTF_16("UTF-16");

    private String name;

    private CharSets(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
