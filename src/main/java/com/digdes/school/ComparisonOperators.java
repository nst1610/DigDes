package com.digdes.school;

// В этом классе реализованы етоды сравнения допустимых типов данных.
// В случае, если оператору передается тип, который им не поддерживается, выбрасывается исключение.

public class ComparisonOperators {
    public static boolean compareString(String operator, String first, String second) throws TypeException {
        return switch (operator) {
            case "=" -> first.equals(second);
            case "!=" -> !first.equals(second);
            case "like" -> first.matches(".*" + second.replaceAll("%", "") + ".*");
            case "ilike" -> first.toLowerCase().matches(".*" + second.replaceAll("%", "").toLowerCase() + ".*");
            default -> throw new TypeException("Тип String не поддерживается оператором " + operator);
        };
    }

    public static boolean compareBoolean(String operator, Boolean first, Boolean second) throws TypeException {
        return switch (operator) {
            case "=" -> first == second;
            case "!=" -> first != second;
            default -> throw new TypeException("Тип Boolean не поддерживается оператором " + operator);
        };
    }

    public static boolean compareLong(String operator, Long first, Long second) throws TypeException {
        return switch (operator) {
            case "=" -> first.equals(second);
            case "!=" -> !first.equals(second);
            case ">=" -> first >= second;
            case "<=" -> first <= second;
            case ">" -> first > second;
            case "<" -> first < second;
            default -> throw new TypeException("Тип Long не поддерживается оператором " + operator);
        };
    }

    public static boolean compareDouble(String operator, Double first, Double second) throws TypeException {
        return switch (operator) {
            case "=" -> first.equals(second);
            case "!=" -> !first.equals(second);
            case ">=" -> first >= second;
            case "<=" -> first <= second;
            case ">" -> first > second;
            case "<" -> first < second;
            default -> throw new TypeException("Тип Double не поддерживается оператором " + operator);
        };
    }
}
