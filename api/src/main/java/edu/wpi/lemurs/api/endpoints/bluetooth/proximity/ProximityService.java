package edu.wpi.lemurs.api.endpoints.bluetooth.proximity;

import edu.wpi.lemurs.api.exceptions.UnauthenticatedException;
import edu.wpi.lemurs.api.exceptions.UnauthorizedException;
import edu.wpi.lemurs.api.security.SecurityService;
import edu.wpi.lemurs.api.security.roles.LemursRole;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProximityService {
    private final SecurityService securityService;
    private final ProximityRepository proximityRepository;

    @Autowired
    public ProximityService(ProximityRepository proximityRepository, SecurityService securityService) {
        this.proximityRepository = proximityRepository;
        this.securityService = securityService;
    }

    public void recordProximity(ProximityDto proximityDto)
        throws UnauthenticatedException, UnauthorizedException {
        securityService.assertHasRole(LemursRole.USER);
        Proximity proximity = new Proximity(
            null,
            securityService.getUser().getId(),
            proximityDto.getTimestamp(),
            proximityDto.getNumberOfDevices()
        );
        proximityRepository.save(proximity);
    }
}
