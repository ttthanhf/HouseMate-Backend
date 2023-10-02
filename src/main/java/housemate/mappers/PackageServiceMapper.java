package housemate.mappers;

import java.time.LocalDateTime;

import housemate.entities.PackageService;
import housemate.models.PackageServiceDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageServiceMapper {

	public static PackageService mapFrNewPackageServiceDTO(
			PackageServiceDTO packageDto) {
		return PackageService.builder()
				.titleName(packageDto.getTitleName())
				.salePrice(packageDto.getSalePrice())
				.description(packageDto.getDescription())
				.saleStatus(packageDto.getSaleStatus())
				.creatorId(packageDto.getCreatorId())
				.createdAt(LocalDateTime.now())
				.packageServiceItemList(
						packageDto.getPackageServiceItem(
								packageDto.getChildServices()
								)
						)
				.build();
	}
	
//	public static PackageService mapFrUpdatePackageServiceDTO(
//		
//	}

}
