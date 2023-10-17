package housemate.models;

import housemate.constants.Enum.GroupType;
import housemate.entities.ServiceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class PurchasedServiceDTO {
    private int serviceId;
    private String titleName;
    private GroupType groupType;
    private List<ServiceType> typeList;

    public PurchasedServiceDTO(int serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PurchasedServiceDTO otherService = (PurchasedServiceDTO) obj;
        return serviceId == otherService.serviceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}
