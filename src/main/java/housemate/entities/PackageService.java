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
@Table(name = "Package_Service")
public class PackageService {

    @Id
    @Column(name = "package_service_id")
    private int id;

    @Column(name = "title_name", unique = true)
    private String titleName;

    @Column(name = "sale_price")
    private int salePrice;

    @Column(name = "description", length = 5000)
    private String description;

    @Column(name = "sale_status")
    private String saleStatus;

    @Column(name = "rating")
    private float rating;

    @Column(name = "creator_id")
    private int creatorId;

    @Column(name = "created_at")
    private int createdAt;

    public PackageService(int id, String titleName, int salePrice, String description, String saleStatus, float rating, int creatorId, int createdAt) {
        this.id = id;
        this.titleName = titleName;
        this.salePrice = salePrice;
        this.description = description;
        this.saleStatus = saleStatus;
        this.rating = rating;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
    }

    
}

