package org.example.Model;

public interface PrinterDataListener {
    void onDataReceived(String data);
    void onStatusUpdate(String status);
}