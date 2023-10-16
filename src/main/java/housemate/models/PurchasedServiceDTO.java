package housemate.models;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class PurchasedServiceDTO {
    private int serviceId;
    private String titleName;
    private String groupType;
    private List<String> typeList;

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
