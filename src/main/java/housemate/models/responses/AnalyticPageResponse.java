/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models.responses;

import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class AnalyticPageResponse {

    private String year;
    private String month;
    private String day;
    private String pageTitle;
    private String sessions;
    private String newUsers;
    private String activeUsers;
    private String eventCount;
}
