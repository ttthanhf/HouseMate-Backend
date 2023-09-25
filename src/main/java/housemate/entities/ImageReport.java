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
@Table(name = "Image_Report")
public class ImageReport {

    @Id
    @Column(name = "image_report_id")
    private int id;

    @Column(name = "task_report_id")
    private int taskReportId;

    @Column(name = "uploader_id")
    private Integer uploaderId;

    public ImageReport(int id, int taskReportId, Integer uploaderId) {
        this.id = id;
        this.taskReportId = taskReportId;
        this.uploaderId = uploaderId;
    }

    
}
