package com.group100.wms.model;

/**
 * Represents a goods supplier for purchase orders.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private with public getter and setter methods providing controlled access
 * - Abstraction: The class offers a straightforward interface for working with supplier information 
 *   without exposing how the data is stored internally
 */
public class Supplier {
    
    // Unique identifier for the supplier in the database
    private int id;
    
    // Official name of the supplier company or individual
    private String name;
    
    // Name of the primary contact person at the supplier
    private String contactPerson;
    
    // Phone number of the supplier or contact person
    private String phone;
    
    // Email address for communication with the supplier
    private String email;
    
    // Physical or postal address of the supplier
    private String address;
    
    // Indicates whether this supplier is currently active/usable in the system
    private boolean isActive;

    // Default constructor - useful for creating empty supplier objects or for frameworks
    public Supplier() {}

    /**
     * Parameterized constructor to create a fully initialized Supplier object
     * @param id unique supplier identifier
     * @param name supplier's company/individual name
     * @param contactPerson name of the main contact person
     * @param phone contact phone number
     * @param email contact email address
     * @param address supplier's physical/postal address
     * @param isActive whether the supplier is currently active
     */
    public Supplier(int id, String name, String contactPerson,
                    String phone, String email, String address, boolean isActive) {
        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.isActive = isActive;
    }

    /**
     * Gets the unique identifier of this supplier
     * @return the supplier ID
     */
    public int getId() { return id; }

    /**
     * Gets the name of the supplier
     * @return supplier name
     */
    public String getName() { return name; }

    /**
     * Gets the name of the primary contact person
     * @return contact person's name
     */
    public String getContactPerson() { return contactPerson; }

    /**
     * Gets the phone number for contacting the supplier
     * @return phone number
     */
    public String getPhone() { return phone; }

    /**
     * Gets the email address for the supplier
     * @return email address
     */
    public String getEmail() { return email; }

    /**
     * Gets the physical or mailing address of the supplier
     * @return supplier address
     */
    public String getAddress() { return address; }

    /**
     * Checks if this supplier is currently active in the system
     * @return true if active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the supplier's name
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets or updates the name of the primary contact person
     * @param contactPerson the contact person's name to set
     */
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    /**
     * Sets or updates the supplier's phone number
     * @param phone the phone number to set
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Sets or updates the supplier's email address
     * @param email the email to set
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Sets or updates the supplier's address
     * @param address the address to set
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * Sets the active status of this supplier
     * @param active true to mark as active, false to mark as inactive
     */
    public void setActive(boolean active) { this.isActive = active; }

    /**
     * Returns a string representation of the Supplier object (useful for logging/debugging)
     * @return string containing id and name
     */
    @Override
    public String toString() {
        return "Supplier{id=" + id + ", name='" + name + "'}";
    }
}
