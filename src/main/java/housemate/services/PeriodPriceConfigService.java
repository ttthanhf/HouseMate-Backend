package housemate.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import housemate.constants.Role;
import housemate.entities.PeriodPriceConfig;
import housemate.models.PeriodConfigNewDTO;
import housemate.repositories.PeriodPriceConfigRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PeriodPriceConfigService {

	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");

	@Autowired
	PeriodPriceConfigRepository perCofgRepo;

	ModelMapper mapper = new ModelMapper();

	@Autowired
	private AuthorizationUtil authorizationUtil;

	// view

	public ResponseEntity<?> getPeriodPriceConfigListInUsed() {
		List<PeriodPriceConfig> peConfgList = perCofgRepo.findAllInUsed(LocalDateTime.now(dateTimeZone));
		if (peConfgList.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found !");
		return ResponseEntity.ok(peConfgList);
	}

	public ResponseEntity<?> getPeriodPriceConfigList() {
		List<PeriodPriceConfig> peConfgList = perCofgRepo.findAll(Sort.by("configValue"));
		if (peConfgList.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found !");
		return ResponseEntity.ok(peConfgList);
	}

	@Transactional
	public ResponseEntity<?> createPeriodPriceConfig(HttpServletRequest request, PeriodConfigNewDTO newPeriodConfig) {

		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");

		PeriodPriceConfig newPeriodPriceProporConfig = mapper.map(newPeriodConfig, PeriodPriceConfig.class);

		if (newPeriodPriceProporConfig.getMin() >= newPeriodPriceProporConfig.getMax())
			return ResponseEntity.badRequest().body("Set the min < max");
		// check if have the same configvalue and configname in db
		// if the differ min-max --> set the time end to now to end in used
		PeriodPriceConfig existedPeriodConfig = perCofgRepo.findByConfigValueAndConfigName(
				LocalDateTime.now(dateTimeZone), newPeriodPriceProporConfig.getConfigValue(),
				newPeriodPriceProporConfig.getConfigName());
		// existed same min max value
		if (existedPeriodConfig != null)
			if (existedPeriodConfig.equals(newPeriodPriceProporConfig))
				return ResponseEntity.ok("Has existed before !\n " + existedPeriodConfig);
			else
				// same config value and name differ min max -> create new one
				existedPeriodConfig.setDateEnd(LocalDateTime.now(dateTimeZone));

		newPeriodPriceProporConfig.setDateStart(LocalDateTime.now(dateTimeZone));
		newPeriodPriceProporConfig.setDateEnd(LocalDateTime.of(2100, 12, 31, 6, 0));
		PeriodPriceConfig savedPeriodPriceConfig = perCofgRepo.save(newPeriodPriceProporConfig);

		if (savedPeriodPriceConfig == null) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed ! ");
		}

		return ResponseEntity.ok(savedPeriodPriceConfig);
	}

	@Transactional
	public ResponseEntity<?> updatePeriodPriceConfig(HttpServletRequest request, PeriodConfigNewDTO newPeriodConfig) {

		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");

		PeriodPriceConfig newPeriodPriceProporConfig = mapper.map(newPeriodConfig, PeriodPriceConfig.class);
		if (newPeriodPriceProporConfig.getMin() >= newPeriodPriceProporConfig.getMax())
			return ResponseEntity.badRequest().body("Set the min < max");
		// check if have the same configvalue and configname in db
		// if the same min-max --> set the time end to now to end in used
		PeriodPriceConfig existedPeriodConfig = perCofgRepo.findByConfigValueAndConfigName(
				LocalDateTime.now(dateTimeZone), newPeriodPriceProporConfig.getConfigValue(),
				newPeriodPriceProporConfig.getConfigName());
		if (existedPeriodConfig == null)
			return ResponseEntity.badRequest().body("Not found to update. Let create new one !");
		if (existedPeriodConfig != null)
			if (existedPeriodConfig.equals(newPeriodPriceProporConfig))
				return ResponseEntity.ok("Has existed before !\n " + existedPeriodConfig);

		existedPeriodConfig.setDateEnd(LocalDateTime.now(dateTimeZone));
		newPeriodPriceProporConfig.setDateStart(LocalDateTime.now(dateTimeZone));
		newPeriodPriceProporConfig.setDateEnd(LocalDateTime.of(2100, 12, 31, 6, 0));
		PeriodPriceConfig savedPeriodPriceConfig = perCofgRepo.save(newPeriodPriceProporConfig);

		if (savedPeriodPriceConfig == null) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Something Error ! Saved Failed ! ");
		}

		return ResponseEntity.ok(savedPeriodPriceConfig);
	}

}
