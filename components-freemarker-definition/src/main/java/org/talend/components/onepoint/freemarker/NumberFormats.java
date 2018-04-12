package org.talend.components.onepoint.freemarker;

/**
 * Enumeration of pre-defined number formats in Freemarker.
 */
public enum NumberFormats implements NamedEnum {

    NUMBER  ("number"),
    CURRENCY("currency"),
    PERCENT ("percent"),
    COMPUTER("computer");

    private String name;

    NumberFormats(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
