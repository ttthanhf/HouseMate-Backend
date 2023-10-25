/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.ImageType;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class UploadDTO {

    private int entityId;
    private ImageType imageType;
}
