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
@Table(name = "Staff_Major")
public class StaffMajor {

    @Id
    @Column(name = "staff_id")
    private int staffId;

    @Id
    @Column(name = "major_id")
    private int majorId;

    public StaffMajor(int staffId, int majorId) {
        this.staffId = staffId;
        this.majorId = majorId;
    }

}
