package com.digdes.school;

import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {
        public JavaSchoolStarter(){

        }

        //На вход запрос, на выход результат выполнения запроса
        public List<Map<String,Object>> execute(String request) throws Exception {
            // Получаем таблицу.
            Table table = Table.getTable();

            // В зависимости от запроса выполняем операцию с таблицей.
            // Если команда введенная в запросе не соответствует допустимым, выбрасывается исключение.
            if(request.matches("(?i)insert values.*"))
                return table.insert(request.replaceAll("(?i)insert values ", ""));

            else if (request.matches("(?i)update values.*"))
                return table.update(request.replaceAll("(?i)update values ", ""));

            else if (request.matches("(?i)delete.*"))
                return table.delete(request.replaceAll("(?i)delete", ""));

            else if (request.matches("(?i)select.*"))
                return table.select(request.replaceAll("(?i)select", ""));
            else
                throw new CommandException("Такая команда не поддерживатеся.");
        }

    }
