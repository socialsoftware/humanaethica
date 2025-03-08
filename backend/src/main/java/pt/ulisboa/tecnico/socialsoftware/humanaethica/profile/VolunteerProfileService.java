package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.domain.VolunteerProfile;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.repository.VolunteerProfileRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.Optional;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class VolunteerProfileService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerProfileRepository volunteerProfileRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<VolunteerProfileDto> getVolunteerProfile(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);
        userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND));

        return volunteerProfileRepository.getProfileForVolunteerId(userId).stream()
                .map(VolunteerProfileDto::new)
                .findFirst();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public VolunteerProfileDto createVolunteerProfile(Integer userId, VolunteerProfileDto volunteerProfileDto) {
        if (volunteerProfileDto == null) throw  new HEException(VOLUNTEER_PROFILE_SHORT_BIO_TOO_SHORT);

        if (userId == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(userId).orElseThrow(() -> new HEException(USER_NOT_FOUND, userId));

        VolunteerProfile volunteerProfile = new VolunteerProfile(volunteer, volunteerProfileDto);
        volunteerProfileRepository.save(volunteerProfile);

        return new VolunteerProfileDto(volunteerProfile);
    }
}