package org.example.Model;

public class TemplateField {
    private int number;
    private String fieldName;
    private String text;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TemplateField(int number, String fieldName, String text) {
        this.number = number;
        this.fieldName = fieldName;
        this.text = text;
    }
}