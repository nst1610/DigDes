package com.digdes.school;

// Исключнние, которое выбрасывается в случае, если все ячейки строки пустые.
public class EmptyRowException extends Exception{
    public EmptyRowException(String message){
        super(message);
    }
}
