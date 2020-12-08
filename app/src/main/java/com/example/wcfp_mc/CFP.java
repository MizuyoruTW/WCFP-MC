package com.example.wcfp_mc;

public class CFP {
    private String Event;
    private String Name;
    private String Time;
    private String Deadline;
    private String URL;

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getEvent() {
        return Event;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTime() {
        return Time;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }
}
