package com.example.eventure.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventure.model.enums.ApprovalStatus;
import com.example.eventure.model.enums.UserRole;

import java.util.List;

public class User implements Parcelable{
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String profileImageId;
    private UserRole role;
    private Boolean active;
    private String email;
    private String password;
    private String companyId;
    private List<EmployeeWorkingHours> workingSchedules;
    public User() {
    }

    public User(String id, String firstName, String lastName, String email, String password, String phone, String address, String profileImageId, UserRole role, Boolean active, String companyId, List<EmployeeWorkingHours> ewh) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.profileImageId = profileImageId;
        this.role = role;
        this.active = active;
        this.email = email;
        this.companyId = companyId;
        this.workingSchedules = ewh;
        this.password = password;
    }

    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.lastName;
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.profileImageId = user.profileImageId;
        this.role = user.getRole();
        this.active = true;
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<EmployeeWorkingHours> getWorkingSchedules() {
        return workingSchedules;
    }

    public void setWorkingSchedules(List<EmployeeWorkingHours> workingSchedules) {
        this.workingSchedules = workingSchedules;
    }

    public User(Parcel in) {
        this.id = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.profileImageId = in.readString();
        this.role = UserRole.valueOf(in.readString());
        this.active = in.readBoolean();
        this.email = in.readString();
        this.companyId = in.readString();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImageId() {
        return profileImageId;
    }

    public void setProfileImageId(String profileImageId) {
        this.profileImageId = profileImageId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(profileImageId);
        if (role != null) {
            dest.writeString(role.name());
        } else {
            dest.writeString(null);
        }
        dest.writeBoolean(active);
        dest.writeString(email);
        dest.writeString(companyId);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
