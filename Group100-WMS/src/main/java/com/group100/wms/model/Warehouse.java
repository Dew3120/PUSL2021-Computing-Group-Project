package com.group100.wms.model;

/**
 * Represents a physical warehouse location.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access is controlled through public getter and setter methods
 * - Abstraction: Provides a simple, high-level interface to warehouse information without exposing internal details
 */
public class Warehouse {
    
    // Unique identifier for the warehouse in the database
    private int id;
    
    // Official or display name of the warehouse (e.g., "Main Warehouse", "Colombo Branch")
    private String name;
    
    // Physical address or location description of the warehouse
    private String location;
    
    // Name of the person currently assigned as the warehouse manager
    private String managerName;
    
    // Indicates whether this warehouse is currently operational/active in the system
    private boolean isActive;

    // Default constructor - useful for creating empty warehouse objects or for frameworks
    public Warehouse() {}

    /**
     * Parameterized constructor to create a fully initialized Warehouse object
     * @param id unique warehouse identifier
     * @param name name of the warehouse
     * @param location physical location/address
     * @param managerName name of the assigned manager
     * @param isActive whether the warehouse is currently active
     */
    public Warehouse(int id, String name, String location,
                     String managerName, boolean isActive) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.managerName = managerName;
        this.isActive = isActive;
    }

    /**
     * Gets the unique identifier of this warehouse
     * @return the warehouse ID
     */
    public int getId() { return id; }

    /**
     * Gets the name of this warehouse
     * @return warehouse name
     */
    public String getName() { return name; }

    /**
     * Gets the physical location of this warehouse
     * @return location description
     */
    public String getLocation() { return location; }

    /**
     * Gets the name of the current warehouse manager
     * @return manager's name
     */
    public String getManagerName() { return managerName; }

    /**
     * Checks if this warehouse is currently active/operational
     * @return true if active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the name of the warehouse
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets or updates the physical location of the warehouse
     * @param location the location to set
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Assigns or updates the name of the warehouse manager
     * @param managerName the manager name to set
     */
    public void setManagerName(String managerName) { this.managerName = managerName; }

    /**
     * Sets the active/operational status of this warehouse
     * @param active true to mark as active, false to mark as inactive
     */
    public void setActive(boolean active) { this.isActive = active; }

    /**
     * Returns a string representation of the Warehouse object (useful for logging/debugging)
     * @return string containing id, name, and location
     */
    @Override
    public String toString() {
        return "Warehouse{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}
