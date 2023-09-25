/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "User_Address")
public class UserAddress {

    @Id
    @Column(name = "address_id")
    private int addressId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "city")
    private String city;

    @Column(name = "urban_district")
    private String urbanDistrict;

    @Column(name = "ward")
    private String ward;

    @Column(name = "address")
    private String address;

    public UserAddress(int addressId, int userId, String city, String urbanDistrict, String ward, String address) {
        this.addressId = addressId;
        this.userId = userId;
        this.city = city;
        this.urbanDistrict = urbanDistrict;
        this.ward = ward;
        this.address = address;
    }

    
}

