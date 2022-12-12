package mj.provisioning.profile.application.service;

import mj.provisioning.profile.application.port.in.ProfileUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ProfileService implements ProfileUseCase {
}
