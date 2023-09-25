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
@Table(name = "Major")
public class Major {

    @Id
    @Column(name = "major_id")
    private int id;

    @Column(name = "major_name", unique = true)
    private String majorName;

    public Major(int id, String majorName) {
        this.id = id;
        this.majorName = majorName;
    }
}

