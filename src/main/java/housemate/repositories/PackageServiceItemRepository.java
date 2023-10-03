package housemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import housemate.entities.PackageServiceItem;

@Repository
public interface PackageServiceItemRepository extends JpaRepository<PackageServiceItem, Integer[]>{

	PackageServiceItem findByPackageIdAndServiceId(int packageId, int serviceId);
	
}
