package io.kairos.maps.apps.calling;

import java.util.Comparator;

public class ContactInfo implements Comparator<ContactInfo> {
    private int id;
    private String name;
    private String phoneNumber;
    private String thumbnail;
    private String order;

    public ContactInfo(int id, String name, String phoneNumber, String thumbnail, String order) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.thumbnail = thumbnail;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public int compare(ContactInfo lhs, ContactInfo rhs) {
        return lhs.order.compareTo(rhs.order);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        if (id != that.id) return false;
        if (!phoneNumber.equals(that.phoneNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + phoneNumber.hashCode();
        return result;
    }
}
