package com.example.easytodo.utils;

import static java.lang.String.format;

import androidx.core.util.Pair;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeExtractor {
    public static class ExtractedData {
        public String date;
        public String time;
        public int start;
        public int end;
        public boolean found;

        ExtractedData() {
            this.found = false;
        }

        public ExtractedData(Date date, Time time, int start, int end) {
            String year = (date.getYear() + 1900) + "";
            String month = date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth() + "";
            String day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate() + "";
            String hour = time.getHours() < 10 ? "0" + time.getHours() : time.getHours() + "";
            String minute = time.getMinutes() < 10 ? "0" + time.getMinutes() : time.getMinutes() + "";
            this.date = year + "-" + month + "-" + day;
            this.time = hour + ":" + minute;
            this.start = start;
            this.end = end;
            found = true;
        }

        ExtractedData(Date date, int start, int end) {
            String year = (date.getYear() + 1900) + "";
            String month = date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth() + "";
            String day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate() + "";
            this.date = year + "-" + month + "-" + day;
            this.start = start;
            this.end = end;
            found = true;
        }

        ExtractedData(Time time, int start, int end) {
            String hour = time.getHours() < 10 ? "0" + time.getHours() : time.getHours() + "";
            String minute = time.getMinutes() < 10 ? "0" + time.getMinutes() : time.getMinutes() + "";
            LocalDateTime now = LocalDateTime.now();
            String year = now.getYear() + "";
            String month = now.getMonthValue() < 10 ? "0" + now.getMonthValue() : now.getMonthValue() + "";
            String day = now.getDayOfMonth() < 10 ? "0" + now.getDayOfMonth() : now.getDayOfMonth() + "";
            this.date = year + "-" + month + "-" + day;
            this.time = hour + ":" + minute;
            this.start = start;
            this.end = end;
            found = true;
        }
    }

    public static ExtractedData full_time_matcher(String txt) {
        int start;
        int end;
        String regex = "(1[0-2]|0?[1-9]):[0-5][0-9] *(am|pm)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String time_txt = txt.substring(start, end);
            // find first number
            Pair<Integer, Integer> first_num = find_first_number(time_txt, 0);
            if (first_num.first == -1) {
                return new ExtractedData();
            }
            // find second number
            Pair<Integer, Integer> second_num = find_first_number(time_txt, first_num.second);
            if (second_num.first == -1) {
                return new ExtractedData();
            }

            Time time;
            if (time_txt.contains("pm")) {
                if (first_num.first != 12) {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", first_num.first + 12));
                } else {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", first_num.first));
                }
            } else {
                if (first_num.first == 12) {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", 0));
                } else {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", first_num.first));
                }
            }
            return new ExtractedData(time, start, end);
        } else {
            return new ExtractedData();
        }
    }

    public static ExtractedData half_time_matcher(String txt) {
        int start;
        int end;
        String regex = "at *(1[0-2]|0?[1-9]) *(am|pm)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String time_txt = txt.substring(start, end);
            // find first number
            Pair<Integer, Integer> first_num = find_first_number(time_txt, 0);
            if (first_num.first == -1) {
                return new ExtractedData();
            }
            Time time;
            if (time_txt.contains("pm")) {
                if (first_num.first != 12) {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", first_num.first + 12));
                } else {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", first_num.first));
                }
            } else {
                if (first_num.first == 12) {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", 0));
                } else {
                    time = Time.valueOf(format(Locale.US, "%02d:00:00", first_num.first));
                }
            }
            return new ExtractedData(time, start, end);
        } else {
            return new ExtractedData();
        }
    }

    public static ExtractedData full_date_matcher(String txt) {
        int start;
        int end;
        String regex = "(3[01]|[12][0-9]|0?[1-9])(/|-| |.)(1[0-2]|0?[1-9])(/|-| |.)20\\d\\d";
        String regex2 = "(3[01]|[12][0-9]|0?[1-9])(/|-| |.)(1[0-2]|0?[1-9])(/|-| |.)\\d\\d";
        Pattern pattern = Pattern.compile(regex);
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher = pattern.matcher(txt);
        Matcher matcher2 = pattern2.matcher(txt);
        boolean first_match = matcher.find();
        boolean second_match = matcher2.find();
        if (first_match || second_match) {
            if (first_match) {
                start = matcher.start();
                end = matcher.end();
            } else {
                start = matcher2.start();
                end = matcher2.end();
            }
            String date_txt = txt.substring(start, end);
            Pair<Integer, Integer> first_num = find_first_number(date_txt, 0);
            if (first_num.first == -1) {
                return new ExtractedData();
            }
            Pair<Integer, Integer> second_num = find_first_number(date_txt, first_num.second);
            if (second_num.first == -1) {
                return new ExtractedData();
            }
            Pair<Integer, Integer> third_num = find_first_number(date_txt, second_num.second);
            if (third_num.first == -1) {
                return new ExtractedData();
            }
            if (third_num.first < 1000) {
                third_num = new Pair<>(third_num.first + 2000, third_num.second);
            }
            Calendar cal = Calendar.getInstance();
            cal.set(third_num.first, second_num.first - 1, first_num.first);
            Date date = cal.getTime();
            return new ExtractedData(date, start, end);
        } else {
            return new ExtractedData();
        }
    }

    public static ExtractedData half_date_matcher(String txt) {
        int start;
        int end;
        String regex = "on (3[01]|[12][0-9]|0?[1-9])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String date_txt = txt.substring(start, end);
            Pair<Integer, Integer> first_num = find_first_number(date_txt, 0);
            if (first_num.first == -1) {
                return new ExtractedData();
            }
            // if less than current day, then it is in the next month
            int month;
            if (first_num.first < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                month = Calendar.getInstance().get(Calendar.MONTH) + 2;
            } else {
                month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            }
            // if less than current month, then it is in the next year
            int year;
            if (month < Calendar.getInstance().get(Calendar.MONTH)) {
                year = Calendar.getInstance().get(Calendar.YEAR) + 1;
            } else {
                year = Calendar.getInstance().get(Calendar.YEAR);
            }
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, first_num.first);
            Date date = cal.getTime();
            return new ExtractedData(date, start, end);
        } else {
            return new ExtractedData();
        }
    }

    public static ExtractedData after_matcher(String txt) {
        int start;
        int end;
        String regex = "after [0-9]+ (minute|hour|day|week|month|year)s?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String time_txt = txt.substring(start, end);
            Pair<Integer, Integer> first_num = find_first_number(time_txt, 0);
            if (first_num.first == -1) {
                return new ExtractedData();
            }
            String time_unit = time_txt.substring(first_num.second);
            Calendar cal = Calendar.getInstance();
            if (time_unit.contains("minute")) {
                cal.add(Calendar.MINUTE, first_num.first);
            } else if (time_unit.contains("hour")) {
                cal.add(Calendar.HOUR, first_num.first);
            } else if (time_unit.contains("day")) {
                cal.add(Calendar.DAY_OF_MONTH, first_num.first);
            } else if (time_unit.contains("week")) {
                cal.add(Calendar.WEEK_OF_MONTH, first_num.first);
            } else if (time_unit.contains("month")) {
                cal.add(Calendar.MONTH, first_num.first);
            } else if (time_unit.contains("year")) {
                cal.add(Calendar.YEAR, first_num.first);
            }
            Date date = cal.getTime();
            Time time = new Time(date.getTime());
            return new ExtractedData(date, time, start, end);
        } else {
            return new ExtractedData();
        }
    }

    public static Pair<Integer, Integer> find_first_number(String txt, int start) {
        int first_num = -1;
        int last_num = -1;
        for (int i = start; i < txt.length(); i++) {
            if (Character.isDigit(txt.charAt(i))) {
                first_num = i;
                break;
            }
        }
        for (int i = first_num; i < txt.length(); i++) {
            if (!Character.isDigit(txt.charAt(i))) {
                last_num = i;
                break;
            }
        }
        if (last_num == -1 && first_num != -1
                && Character.isDigit(txt.charAt(txt.length() - 1))) {
            last_num = txt.length();
        }
        if (first_num == -1 || last_num == -1) {
            return new Pair<>(-1, -1);
        }
        int number = Integer.parseInt(txt.substring(first_num, last_num));
        return new Pair<>(number, last_num);
    }
}
