package com.example.busservicedriverapp.modelclass;

public class Report {
    String report_body,report_date,repoter_name;

    public Report() {
    }

    public Report(String report_body, String report_date, String repoter_name) {
        this.report_body = report_body;
        this.report_date = report_date;
        this.repoter_name = repoter_name;
    }

    public String getReport_body() {
        return report_body;
    }

    public void setReport_body(String report_body) {
        this.report_body = report_body;
    }

    public String getReport_date() {
        return report_date;
    }

    public void setReport_date(String report_date) {
        this.report_date = report_date;
    }

    public String getRepoter_name() {
        return repoter_name;
    }

    public void setRepoter_name(String repoter_name) {
        this.repoter_name = repoter_name;
    }
}
