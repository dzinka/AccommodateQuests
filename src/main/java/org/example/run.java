package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

class Main {

    public static boolean checkCapacity(int maxCapacity, List<Map<String, String>> guests) {
        List<Event> events = new ArrayList<>();

        for (Map<String, String> guest : guests) {
            String checkIn = guest.get("check-in");
            String checkOut = guest.get("check-out");
            events.add(new Event(checkIn, 1)); // заезд
            events.add(new Event(checkOut, -1)); // выезд
        }

        // Сортируем события: сначала по дате, затем выезды перед заездами при одинаковой дате
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                int dateCompare = e1.date.compareTo(e2.date);
                if (dateCompare != 0) {
                    return dateCompare;
                }
                return Integer.compare(e1.delta, e2.delta);
            }
        });

        int currentGuests = 0;
        for (Event event : events) {
            currentGuests += event.delta;
            if (currentGuests > maxCapacity) {
                return false;
            }
        }

        return true;
    }

    static class Event {
        String date;
        int delta; // +1 для заезда, -1 для выезда

        Event(String date, int delta) {
            this.date = date;
            this.delta = delta;
        }
    }

    // Вспомогательный метод для парсинга JSON строки в Map
    private static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        // Удаляем фигурные скобки
        json = json.substring(1, json.length() - 1);

        // Разбиваем на пары ключ-значение
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }
        return map;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Первая строка - вместимость гостиницы
        int maxCapacity = Integer.parseInt(scanner.nextLine());

        // Вторая строка - количество записей о гостях
        int n = Integer.parseInt(scanner.nextLine());

        List<Map<String, String>> guests = new ArrayList<>();

        // Читаем n строк, json-данные о посещении
        for (int i = 0; i < n; i++) {
            String jsonGuest = scanner.nextLine();
            // Простой парсер JSON строки в Map
            Map<String, String> guest = parseJsonToMap(jsonGuest);
            guests.add(guest);
        }

        // Вызов функции
        boolean result = checkCapacity(maxCapacity, guests);

        // Вывод результата
        System.out.println(result ? "True" : "False");

        scanner.close();
    }
}