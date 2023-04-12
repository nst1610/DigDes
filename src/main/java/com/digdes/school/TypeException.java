package com.digdes.school;

// Исключение выбрасывется, если для данного типа данных не определен оператор.
// Также выбрасывается в случае, если в сравнении участвует null.
public class TypeException extends Exception{
    public TypeException(String message){
        super(message);
    }
}
