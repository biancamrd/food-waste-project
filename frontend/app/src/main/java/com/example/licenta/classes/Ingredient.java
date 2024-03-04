package com.example.licenta.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Ingredient implements Parcelable {

    @SerializedName("id")
    private Long id;

    @SerializedName("user")
    private User user;

    @SerializedName("name")
    private String name;

    @SerializedName("quantity")
    private Float quantity;

    @SerializedName("unit")
    private String unit;

    @SerializedName("expirationDate")
    private Date expirationDate;

    private boolean isSelected;

    private int position;

    public Ingredient() {
    }

    public Ingredient(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Ingredient(Long id, String name, Float quantity, String unit, Date expirationDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
    }

    public Ingredient(String name) {
        this.name = name;
        this.quantity = 0f;
        this.unit = null;
        this.expirationDate = new Date();
    }

    protected Ingredient(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        quantity = in.readFloat();
        unit = in.readString();
        long tmpExpirationDate = in.readLong();
        expirationDate = tmpExpirationDate != -1 ? new Date(tmpExpirationDate) : null;
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeFloat(quantity);
        dest.writeString(unit);
        dest.writeLong(expirationDate != null ? expirationDate.getTime() : -1L);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", expirationDate=" + expirationDate +
                ", isSelected=" + isSelected +
                ", position=" + position +
                '}';
    }
}
