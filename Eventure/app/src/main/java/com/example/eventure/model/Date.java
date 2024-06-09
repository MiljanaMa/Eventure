package com.example.eventure.model;

import java.util.Objects;

public class Date implements Comparable<Date>  {
    private int year;
    private int month;
    private int day;

    public Date() {
    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return day + "." + month + "." + year + ".";
    }

    @Override
    public int compareTo(Date otherDate) {
        // Compare years
        if (this.year != otherDate.year) {
            return Integer.compare(this.year, otherDate.year);
        }

        // If years are equal, compare months
        if (this.month != otherDate.month) {
            return Integer.compare(this.month, otherDate.month);
        }

        // If months are equal, compare days
        return Integer.compare(this.day, otherDate.day);
    }
    public boolean isAfter(Date otherDate) {
        if (this.year > otherDate.year) {
            return true;
        } else if (this.year == otherDate.year) {
            if (this.month > otherDate.month) {
                return true;
            } else if (this.month == otherDate.month) {
                return this.day > otherDate.day;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return year == date.year && month == date.month && day == date.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }
}
