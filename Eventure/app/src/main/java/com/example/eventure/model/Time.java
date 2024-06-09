package com.example.eventure.model;

public class Time implements Comparable<Time>{
    private int hours;
    private int minutes;

    public Time() {
    }

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return hours + ":" + minutes;
    }

    @Override
    public int compareTo(Time o) {
        if (this.hours != o.hours) {
            return Integer.compare(this.hours, o.hours);
        }

        return Integer.compare(this.minutes, o.minutes);
    }
}
