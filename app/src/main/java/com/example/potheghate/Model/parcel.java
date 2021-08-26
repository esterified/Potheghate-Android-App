package com.example.potheghate.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class parcel implements Parcelable {
    private String name;
    private String address;
    private String phone;

    public parcel(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public parcel() {
    }

    protected parcel(Parcel in) {
        name = in.readString();
        address = in.readString();
        phone = in.readString();
    }

    public static final Creator<parcel> CREATOR = new Creator<parcel>() {
        @Override
        public parcel createFromParcel(Parcel in) {
            return new parcel(in);
        }

        @Override
        public parcel[] newArray(int size) {
            return new parcel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
