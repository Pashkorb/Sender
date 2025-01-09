package org.example;

public class PrinterStatus {
    public static String getStatus(String answer){
        String status="";
        answer=answer.substring(4,6);
        switch (answer){
            case ("00"):status="ОК";break;
            case ("01"):status="Ошибка";break;
            case ("02"):status="Очередь данных для печати превышает 1000 строк";break;
            case ("03"):status="Неверная контрольная сумма";break;
            case ("04"):status="Очередь данных для печати пустая";break;
            case ("05"):status="Неподдерживаемая команда";break;
            case ("06"):status="Система занята";break;
            case ("EF"):status="Принтер не готов (not printing state)";break;
            case ("08"):status="The file type is not within acceptable range or unsupported";break;
            case ("09"):status="File is too large or the BMP size is not within 34 dots high";break;
            case ("0A"):status="The defined length of the file is not the same as transmission";break;
            case ("0B"):status="File does not exist";break;
            case ("0C"):status="The file is underling printing, cannot be deleted";break;
        }

        return status;
    }
}
