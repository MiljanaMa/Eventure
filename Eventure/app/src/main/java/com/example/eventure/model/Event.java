package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.PrivacyRules;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class  Event implements Parcelable {
    private String id;
    private String organizerId;
    //private DocumentReference organizer;
    //private DocumentReference eventType;
    private String eventType; //privremeno
    private String eventTypeId;
    private String name;
    private String description;
    private int maxParticipants;
    private String privacyRules;
    private String locationRestrictions;
    private String timeRestrictions;
    private List<String> guestsIds;
    private String budgetId;
    //private DocumentReference budget;
    private String agendaId;

    public Event(){

    }

    public Event(String id, String organizerId, String eventTypeId, String name, String description, int maxParticipants, String privacyRules, String locationRestrictions, String timeRestrictions, List<String> guestsIds, String budgetId, String agendaId) {
        this.id = id;
        this.organizerId = organizerId;
        //this.organizer = organizer;
        //this.eventType = eventType;
        this.eventTypeId = eventTypeId;
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.privacyRules = privacyRules;
        this.locationRestrictions = locationRestrictions;
        this.timeRestrictions = timeRestrictions;
        this.guestsIds = guestsIds;
        this.budgetId = budgetId;
        //this.budget = budget;
        this.agendaId = agendaId;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /*public DocumentReference getOrganizer() {
        return organizer;
    }

    public void setOrganizer(DocumentReference organizer) {
        this.organizer = organizer;
    }

    public DocumentReference getEventType() {
        return eventType;
    }

    public void setEventType(DocumentReference eventType) {
        this.eventType = eventType;
    }*/

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getPrivacyRules() {
        return privacyRules;
    }

    public void setPrivacyRules(String privacyRules) {
        this.privacyRules = privacyRules;
    }

    public String getLocationRestrictions() {
        return locationRestrictions;
    }

    public void setLocationRestrictions(String locationRestrictions) {
        this.locationRestrictions = locationRestrictions;
    }

    public String getTimeRestrictions() {
        return timeRestrictions;
    }

    public void setTimeRestrictions(String timeRestrictions) {
        this.timeRestrictions = timeRestrictions;
    }

    public List<String> getGuestsIds() {
        return guestsIds;
    }

    public void setGuestsIds(List<String> guestsIds) {
        this.guestsIds = guestsIds;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    /*public DocumentReference getBudget() {
        return budget;
    }

    public void setBudget(DocumentReference budget) {
        this.budget = budget;
    }*/

    public String getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }



    protected Event(Parcel in) {
        id = in.readString();
        organizerId = in.readString();
        eventType = in.readString();
        eventTypeId = in.readString();
        name = in.readString();
        description = in.readString();
        maxParticipants = in.readInt();
        privacyRules = in.readString();
        locationRestrictions = in.readString();
        timeRestrictions = in.readString();
        guestsIds = in.createStringArrayList();
        budgetId = in.readString();
        agendaId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(organizerId);
        dest.writeString(eventType);
        dest.writeString(eventTypeId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(maxParticipants);
        dest.writeString(privacyRules);
        dest.writeString(locationRestrictions);
        dest.writeString(timeRestrictions);
        dest.writeStringList(guestsIds);
        dest.writeString(budgetId);
        dest.writeString(agendaId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

}
