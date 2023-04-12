package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// В данном классе реализованы методы, которые направлены на работу с записями в таблице.
public class DataManipulations {

    private DataManipulations(){

    }

    // Метод, обеспечивающий вставку переданных данных в виде строки в переданную таблицу.
    public static List<Map<String, Object>> insertData(List<Map<String, Object>> table, Map<String, Object> dataForInsert)
            throws EmptyRowException{
        // Поля и их значения, соответствующие вставляемой строке.
        Map<String,Object> currentRow = new HashMap<>();

        for(Map.Entry<String, Object> currentField : dataForInsert.entrySet()){
            if(currentField.getValue() instanceof Long)
                currentRow.put(currentField.getKey(), currentField.getValue());
            else if (currentField.getValue() instanceof String)
                currentRow.put(currentField.getKey(), currentField.getValue());
            else if(currentField.getValue() instanceof Double)
                currentRow.put(currentField.getKey(), currentField.getValue());
            else if(currentField.getValue() instanceof Boolean)
                currentRow.put(currentField.getKey(), currentField.getValue());
        }
        // Значения во всех ячейках одной строки не могут быть пустыми.
        if(currentRow.isEmpty())
            throw new EmptyRowException("Все поля не могут быть пустыми.");

        // Строка для вставки.
        List<Map<String, Object>> isertedRow = new ArrayList<>();

        table.add(currentRow);
        isertedRow.add(currentRow);

        return isertedRow;
    }

    // Метод, обеспечивающий изменение данных в переданной таблице на новые значения, которые передаются в метод.
    public static List<Map<String, Object>> updateData(List<Map<String, Object>> table, Map<String, Object> newValues)
            throws TypeException {
        // Строки, которые были изменены.
        List<Map<String, Object>> updatedData = new ArrayList<>();

        // Проходимся во всем строкам таблицы и изменяем значения в соответствующих ячейках.
        for (Map<String, Object> currentRow : table) {
            boolean isUpdated = false;
            for (Map.Entry<String, Object> currentField : newValues.entrySet()) {
                // Если новое значение ячейки равно null, то из строки удаляется эта ячейка.
                if (currentField.getValue() == null) {
                    currentRow.remove(currentField.getKey());
                    isUpdated = true;
                }

                // Изменяются строки, которые содержат заданные поля и имеют значения в этих полях отличные от заданных.
                // Если же в строке в заданной ячейке нет значения, то устанавливаем его равным заданному.
                if (!currentRow.containsKey(currentField.getKey()) || currentRow.containsKey(currentField.getKey()) &&
                        !compareFields(currentRow.get(currentField.getKey()), currentField.getValue(), "=")) {
                    currentRow.put(currentField.getKey(), currentField.getValue());
                    isUpdated = true;
                }
            }
            if (isUpdated)
                updatedData.add(currentRow);
        }

        return updatedData;
    }

    // Метод, обеспечивающий сравнение значений в ячейках.
    private static boolean compareFields(Object first, Object second, String operator) throws TypeException {
        boolean isEquals = true;

        if(first instanceof Long && second instanceof Long)
            isEquals = ComparisonOperators.compareLong(operator, (Long) first, (Long) second);
        else if (first instanceof String && second instanceof String)
            isEquals = ComparisonOperators.compareString(operator, (String) first, (String) second);
        else if(first instanceof Double && second instanceof Double)
            isEquals = ComparisonOperators.compareDouble(operator, (Double) first, (Double) second);
        else if(first instanceof Boolean && second instanceof Boolean)
            isEquals = ComparisonOperators.compareBoolean(operator, (Boolean) first, (Boolean) second);

        return isEquals;
    }

    // Метод, отбирающий из таблицы строки, которые соответствуют заданным условиям.
    public static List<Map<String, Object>> suitableStrings(List<Map<String, Object>> tableForCheck, String conditions)
            throws TypeException, ColumnNameException {
        // Если в каком-либо из условий участвует значение null, выбрасываем исключение.
        if(conditions.matches(".*(?i)null.*"))
            throw new TypeException("null не может участвовать в сранении.");

        // Строки, которые удовлетворяют всем условиям.
        List<Map<String, Object>> suitableStrings = new ArrayList<>();

        // Если в условии отсутсвуют ключевые слова AND или OR без учета регистра.
        // Т.е. проверяем строки таблицы на соответствие только одному условию.
        if(!conditions.matches(".*(?i)(and|or).*")){
            // Переданное условие.
            Map.Entry<String,Object> condition = RequestProcessing.dataToMap(conditions);

            Pattern pattern = Pattern.compile(" (?i)ilike | (?i)like |!=|<=|>=|[=><]");
            Matcher matcher = pattern.matcher(conditions);
            String currentConditionOperator = null;
            while (matcher.find())
                currentConditionOperator = matcher.group().trim();

            // Перебираем строки таблицы.
            for (Map<String, Object> currentRow : tableForCheck) {
                // Если в данной строке данное поле содержит значение
                if (currentRow.containsKey(condition.getKey())) {
                    // и значение в данной ячейке соответствует условию
                    if (compareFields(currentRow.get(condition.getKey()), condition.getValue(), currentConditionOperator))
                        // считаем строку подходящей.
                        suitableStrings.add(currentRow);
                } else {
                    // Если же данная ячейка не содержит значения и в условии оператор "!=", то данную ячейку тоже считаем подходящей.
                    if (currentConditionOperator.equals("!="))
                        suitableStrings.add(currentRow);
                }
            }
            return suitableStrings;
        }

        // В случае, если условие не одно (в условии еть ключевые слова AND или OR без учета регистра), разбиваем условия.
        List<String>[] splitConditions = RequestProcessing.splitConditions(conditions);

        for (Map<String, Object> currentRow : tableForCheck) {
            boolean isSuitable = true;
            // Указывет на то, проверяется ли сейчас первое условие или нет.
            boolean isFirst = true;

            // Счетчки по количеству операторов AND и OR в условии запроса.
            for (int j = 0; j < splitConditions[1].size(); j++) {
                // Строка, соответсвующая текущему проверяемому условию.
                String currentConditionString = isFirst ? splitConditions[0].get(0).trim() : splitConditions[0].get(j + 1).trim();

                Pattern pattern = Pattern.compile(" (?i)ilike | (?i)like |!=|<=|>=|[=><]");
                Matcher matcher = pattern.matcher(currentConditionString);
                String currentConditionOperator = null;
                while (matcher.find())
                    currentConditionOperator = matcher.group().trim();

                Map.Entry<String, Object> currentCondition = RequestProcessing.dataToMap(currentConditionString);
                if (currentRow.containsKey(currentCondition.getKey())) {
                    if (isFirst)
                        isSuitable = compareFields(currentRow.get(currentCondition.getKey()),
                                currentCondition.getValue(), currentConditionOperator);
                    else {
                        if (splitConditions[1].get(j).equals("AND"))
                            isSuitable = isSuitable && compareFields(currentRow.get(currentCondition.getKey()),
                                    currentCondition.getValue(), currentConditionOperator);
                        else if (splitConditions[1].get(j).equals("OR"))
                            isSuitable = isSuitable || compareFields(currentRow.get(currentCondition.getKey()),
                                    currentCondition.getValue(), currentConditionOperator);
                    }
                } else {
                    isSuitable = currentConditionOperator.equals("!=");
                }

                if (j == 0 && isFirst) {
                    isFirst = false;
                    j--;
                }
            }
            if (isSuitable)
                suitableStrings.add(currentRow);
        }

        return suitableStrings;
    }
}
