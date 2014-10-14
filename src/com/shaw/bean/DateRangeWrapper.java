package com.shaw.bean;

import java.util.Date;

public class DateRangeWrapper {
    private Date startDate;
    private Date endDate;
    private String[] names;

    public DateRangeWrapper(Date endDate, Date startDate, String[] names) {
        this.names = names;
        this.endDate = endDate;
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String[] getNames() {
        return names;
    }
}
