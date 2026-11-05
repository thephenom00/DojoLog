package cz.cvut.fel.attendance.service.utils;

import java.security.SecureRandom;
import java.time.DayOfWeek;

import java.time.DayOfWeek;

public class HelpMethods {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword() {
        int length = 6;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String getDayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "Pondělí";
            case TUESDAY:
                return "Úterý";
            case WEDNESDAY:
                return "Středa";
            case THURSDAY:
                return "Čtvrtek";
            case FRIDAY:
                return "Pátek";
            case SATURDAY:
                return "Sobota";
            case SUNDAY:
                return "Neděle";
            default:
                return null;
        }
    }
}

