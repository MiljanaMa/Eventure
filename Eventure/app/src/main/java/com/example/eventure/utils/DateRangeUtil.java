package com.example.eventure.utils;
import java.util.Date;

public class DateRangeUtil {
    private Date start;
    private Date end;

    public DateRangeUtil() {}

    public DateRangeUtil(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean doesOverlap(DateRangeUtil dateRange) {
        return !(this.isAfter(dateRange) || this.isBefore(dateRange));
    }

    public boolean isBefore(DateRangeUtil dateRange) {
        return this.start.before(dateRange.getStart()) && this.end.before(dateRange.getStart());
    }

    public boolean isAfter(DateRangeUtil dateRange) {
        return this.start.after(dateRange.getEnd()) && this.end.after(dateRange.getEnd());
    }

}

