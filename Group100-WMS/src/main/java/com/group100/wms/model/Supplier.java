package com.group100.wms.model;

/**
 * Represents a goods supplier for purchase orders.
 */
public class Supplier {

    private int    id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private boolean isActive;

    public Supplier() {}

    public Supplier(int id, String name, String contactPerson,
                    String phone, String email, String address, boolean isActive) {
        this.id            = id;
        this.name          = name;
        this.contactPerson = contactPerson;
        this.phone         = phone;
        this.email         = email;
        this.address       = address;
        this.isActive      = isActive;
    }

    public int     getId()            { return id; }
    public String  getName()          { return name; }
    public String  getContactPerson() { return contactPerson; }
    public String  getPhone()         { return phone; }
    public String  getEmail()         { return email; }
    public String  getAddress()       { return address; }
    public boolean isActive()         { return isActive; }

    public void setId(int id)                         { this.id = id; }
    public void setName(String name)                   { this.name = name; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public void setPhone(String phone)                 { this.phone = phone; }
    public void setEmail(String email)                 { this.email = email; }
    public void setAddress(String address)             { this.address = address; }
    public void setActive(boolean active)              { this.isActive = active; }

    @Override
    public String toString() {
        return "Supplier{id=" + id + ", name='" + name + "'}";
    }
}