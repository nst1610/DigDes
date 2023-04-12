package com.digdes.school;

//Исключение, которое выбрасывается в случае, если в таблице нет колонки с заднным названием.
public class ColumnNameException extends Exception{
    public ColumnNameException(String message){
        super(message);
    }
}
