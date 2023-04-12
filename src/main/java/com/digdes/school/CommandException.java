package com.digdes.school;

// Исключение выбрасывается, если переданная команда не соответствует ни одной из допустимых.
// Допустимые команды: INSERT VALUES, UPDATE VALUES, DELETE, SELECT (без учета регистра).
public class CommandException extends Exception{
    public CommandException(String message){
        super(message);
    }
}
