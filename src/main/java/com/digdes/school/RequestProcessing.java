package com.digdes.school;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// В данном классе реализованы методы, которые отвечают за парсинг запроса.
public class RequestProcessing {
    private RequestProcessing(){

    }

    // Данный метод разделяет поля на пары ключ->значение.
    public static Map<String, Object> splitFields(String fieldsForSplit) throws ColumnNameException {
        Map<String, Object> splittedFields = new HashMap<>();

        String[] data = fieldsForSplit.split(",");

        for (String currentField : data) {
            Map.Entry<String, Object> currentKeyValue = dataToMap(currentField);
            splittedFields.put(currentKeyValue.getKey(), currentKeyValue.getValue());
        }

        return splittedFields;
    }

    // Данный метод разделяет условия на массив, в котором хранятся списки строк.
    public static List<String>[] splitConditions(String conditionsFromRequest){
        List<String>[] conditionsAndOperators = new List[2];
        // В данном массиве хранятся условия.
        conditionsAndOperators[0] = new LinkedList<>();
        // В данном массиве хранятся логические операторы AND и OR в верхнем регистре.
        conditionsAndOperators[1] = new ArrayList<>();


        conditionsAndOperators[0].addAll(Arrays.stream(conditionsFromRequest.split(" (?i)and|(?i)or ")).toList());

        Pattern pattern = Pattern.compile("(?i)and|(?i)or");
        Matcher matcher = pattern.matcher(conditionsFromRequest);
        while (matcher.find())
            conditionsAndOperators[1].add(matcher.group().toUpperCase());

        return conditionsAndOperators;
    }

    // Данный массив переводит строковое представление поля в пару ключ->значение.
    public static Map.Entry<String,Object> dataToMap(String data) throws ColumnNameException {
        Map<String,Object> dataKeyValue = new HashMap<>();

        String[] dataArr = data.split("(?i)ilike|(?i)like|!=|<=|>=|[=><]");
        String dataKey = dataArr[0].trim().replaceAll("[‘’']", "");
        String dataValue = dataArr[1].trim().replaceAll("[‘’']", "");

        // Находим в названиях колонок таблице подходящее.
        for(int i = 0; i < Table.getNameOfColumns().size(); i ++){
            if(Table.getNameOfColumns().get(i).equalsIgnoreCase(dataKey)){
                dataKey = Table.getNameOfColumns().get(i);
                break;
            }
        }

        // Если в таблице содержится колонка с подходящим названием без учета регистра, возвращаем пару.
        if(Table.getNameOfColumns().contains(dataKey)){
            if(dataValue.equals("null"))
                dataKeyValue.put(dataKey, null);

            else {
                switch (dataKey) {
                    case "id", "age" -> dataKeyValue.put(dataKey, Long.valueOf(dataValue));
                    case "lastName" -> dataKeyValue.put(dataKey, dataValue);
                    case "cost" -> dataKeyValue.put(dataKey, Double.valueOf(dataValue));
                    case "active" -> dataKeyValue.put(dataKey, Boolean.valueOf(dataValue));
                }
            }

            for (Map.Entry<String, Object> e : dataKeyValue.entrySet()) {
                if (e.getKey().equals(dataKey))
                    return e;
            }

        }
        // Если в таблице нет колонки с таким названием, выкидываем исключение.
        else
            throw new ColumnNameException("Колонки с именем " + dataKey + " нет в таблице.");

        return null;
    }
}
