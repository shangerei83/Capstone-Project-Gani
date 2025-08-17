package com.capstone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Address entity representing user addresses
 * 
 * This entity stores shipping and billing addresses for users.
 * 
 * @author Capstone Student
 * @version 1.0.0
 */
@Entity
@Table(name = "addresses")
@EntityListeners(AuditingEntityListener.class)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(nullable = false)
    private String city;

    @Size(max = 100, message = "State/Province cannot exceed 100 characters")
    @Column(name = "state_province")
    private String stateProvince;

    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code")
    private String postalCode;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(nullable = false)
    private String country;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "address_type")
    @Enumerated(EnumType.STRING)
    private AddressType addressType = AddressType.SHIPPING;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Enum for address types
    public enum AddressType {
        SHIPPING, BILLING, BOTH
    }

    // Constructors
    public Address() {}

    public Address(String streetAddress, String city, String country) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper methods
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress);
        if (city != null && !city.isEmpty()) {
            sb.append(", ").append(city);
        }
        if (stateProvince != null && !stateProvince.isEmpty()) {
            sb.append(", ").append(stateProvince);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        if (country != null && !country.isEmpty()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return id != null && id.equals(address.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
