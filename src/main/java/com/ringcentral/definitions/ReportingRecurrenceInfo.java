package com.ringcentral.definitions;
import com.alibaba.fastjson.annotation.JSONField;
public class ReportingRecurrenceInfo
{
    // Pattern (frequency) of recurrence
    public String pattern;
    public ReportingRecurrenceInfo pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }
    // Time and dates should be specified in UTC time zone
    public String value;
    public ReportingRecurrenceInfo value(String value) {
        this.value = value;
        return this;
    }
}
