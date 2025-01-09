package org.example;

public class PrinterCommand {

    private String functionCode;
    private String data;

    public PrinterCommand(String functionCode, String data) {
        this.functionCode = functionCode;
        this.data = data;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public String getData() {
        return data;
    }
}
