package com.thesis.velma;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cathlynjaneamodia on 10/2/2017.
 */

public class EventsEntity {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("UserID")
    @Expose
    private String userID;

    @SerializedName("EventID")
    @Expose
    private long eventID;

    @SerializedName("EventName")
    @Expose
    private String eventName;

    @SerializedName("EventDescription")
    @Expose
    private String eventDescription;

    @SerializedName("EventLocation")
    @Expose
    private String eventLocation;

    @SerializedName("StartDate")
    @Expose
    private String startDate;

    @SerializedName("StartTime")
    @Expose
    private String startTime;

    @SerializedName("EndDate")
    @Expose
    private String endDate;

    @SerializedName("EndTime")
    @Expose
    private String endTime;

    @SerializedName("InvitedFriends")
    @Expose
    private String invitedFriends;

    @SerializedName("Extra1")
    @Expose
    private String extra1;

    @SerializedName("Extra2")
    @Expose
    private String extra2;

    @SerializedName("Extra3")
    @Expose
    private String extra3;

    @SerializedName("Extra4")
    @Expose
    private String extra4;

    public EventsEntity(String userID, long eventID, String eventName, String eventDescription, String eventLocation, String startDate, String startTime, String endDate, String endTime, String invitedFriends, String extra1, String extra2, String extra3, String extra4) {
        this.userID = userID;
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.invitedFriends = invitedFriends;
        this.extra1 = extra1;
        this.extra2 = extra2;
        this.extra3 = extra3;
        this.extra4 = extra4;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getInvitedFriends() {
        return invitedFriends;
    }

    public void setInvitedFriends(String invitedFriends) {
        this.invitedFriends = invitedFriends;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    public String getExtra4() {
        return extra4;
    }

    public void setExtra4(String extra4) {
        this.extra4 = extra4;
    }
}