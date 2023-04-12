package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Класс, представляющий собой таблицу.
public class Table {

    private static Table TABLE;

    private List<Map<String, Object>> table;
    private static List<String> nameOfColumns;
    private Table() {
        nameOfColumns = new ArrayList<>(List.of("id", "lastName", "age", "cost", "active"));
    }

    // Если таблица уже создана, то возвращаем таблицу, если таблица еще не создана, то создаем и возвращаем ее.
    public static Table getTable(){
        if (TABLE == null)
            TABLE = new Table();

        return TABLE;
    }

    public static List<String> getNameOfColumns() {
        return nameOfColumns;
    }

    // Операция вставки значений в таблицу.
    public List<Map<String, Object>> insert(String request) throws CommandException,
            ColumnNameException, EmptyRowException {
        // КОманда в ставки всегда используется без оператора where.
        if(request.matches(".*(?i)where.*"))
            throw new CommandException("where не используется с командой insert.");

        if(table == null){
            table = new ArrayList<>();
        }

        return DataManipulations.insertData(table,  RequestProcessing.splitFields(request));
    }

    // Операция удаления записей из таблицы.
    public List<Map<String, Object>> delete(String request) throws ColumnNameException, TypeException {
        // Строки, которые были удалены.
        List<Map<String, Object>> deletedRows = new ArrayList<>();

        // Если запрос не содержит оператора WHERE без учета регистра, то удаляем все строки таблицы.
        if(!request.matches(".*(?i)where.*")){
            deletedRows.addAll(table);
            table.clear();
            return deletedRows;
        }

        // Находим строки, которые соответствуют условию после оператора WHERE.
        deletedRows = DataManipulations.suitableStrings(table, request.split("(?i)where")[1]);
        // Удяляем из таблицы все строки, которыее соотвествуют условию.
        table.removeAll(deletedRows);

        return deletedRows;
    }


    // Операция выбора строк из таблицы.
    public List<Map<String, Object>> select(String request) throws Exception {
        // Если запрос не содержит оператора WHERE без учета регистра, то возвращием все строки таблицы.
        if(!request.matches(".*(?i)where.*"))
            return table;

        // Возвращаем строки, соответствующие условию после WHERE.
        return DataManipulations.suitableStrings(table,  request.split("(?i)where")[1]);
    }

    // Операция изменения строк в таблице.
    public List<Map<String, Object>> update(String request) throws ColumnNameException, TypeException {

        // Если запрос не содержит оператора WHERE без учета регистра, то изменяем все строки таблицы.
        if(!request.matches(".*(?i)where.*"))
            return DataManipulations.updateData(table, RequestProcessing.splitFields(request));

        String[] valuesAndConditions = request.split("(?i)where");

        // Изменяем строки, которые соотвествуют условию после WHERE.
        return DataManipulations.updateData(DataManipulations.suitableStrings(table,
                valuesAndConditions[1]), RequestProcessing.splitFields(valuesAndConditions[0]));
    }

}
