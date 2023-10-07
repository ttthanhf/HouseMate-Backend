package housemate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import housemate.entities.PackageServiceItem;
import housemate.entities.Service;
import housemate.entities.ServiceType;

@Repository
public interface PackageServiceItemRepository extends JpaRepository<PackageServiceItem, Integer[]>{
	Optional<List<PackageServiceItem>> findAllByPackageServiceId(int packageServiceId);
	
}
