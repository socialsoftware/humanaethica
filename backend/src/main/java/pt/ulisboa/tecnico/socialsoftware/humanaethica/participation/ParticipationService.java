package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Service
public class ParticipationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ParticipationRepository participationRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ParticipationDto> getParticipationsByActivity(Integer activityId) {
        if (activityId == null) throw  new HEException(ACTIVITY_NOT_FOUND);
        activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));

        return participationRepository.getParticipationsByActivityId(activityId).stream()
                .sorted(Comparator.comparing(Participation::getAcceptanceDate))
                .map(participation -> new ParticipationDto(participation, User.Role.MEMBER))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ParticipationDto> getVolunteerParticipations(Integer userId) {
        if (userId == null) throw new HEException(USER_NOT_FOUND);

        return participationRepository.getParticipationsForVolunteerId(userId).stream()
                .sorted(Comparator.comparing(Participation::getAcceptanceDate))
                .map(participation -> new ParticipationDto(participation, User.Role.VOLUNTEER))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto memberRating(Integer participationId, ParticipationDto participationDto) {
        if (participationId == null) throw new HEException(PARTICIPATION_NOT_FOUND);
        Participation participation = participationRepository.findById(participationId).orElseThrow(() -> new HEException(PARTICIPATION_NOT_FOUND, participationId));

        participation.memberRating(participationDto);
        return new ParticipationDto(participation, User.Role.MEMBER);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto volunteerRating(Integer participationId, ParticipationDto participationDto) {
        if (participationId == null) throw new HEException(PARTICIPATION_NOT_FOUND);
        Participation participation = participationRepository.findById(participationId).orElseThrow(() -> new HEException(PARTICIPATION_NOT_FOUND, participationId));

        participation.volunteerRating(participationDto);
        return new ParticipationDto(participation,  User.Role.VOLUNTEER);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto createParticipation(Integer activityId, ParticipationDto participationDto) {
        if (participationDto == null) throw  new HEException(PARTICIPATION_REQUIRES_INFORMATION);

        if (participationDto.getVolunteerId() == null) throw new HEException(USER_NOT_FOUND);
        Volunteer volunteer = (Volunteer) userRepository.findById(participationDto.getVolunteerId()).orElseThrow(() -> new HEException(USER_NOT_FOUND, participationDto.getVolunteerId()));

        if (activityId == null) throw  new HEException(ACTIVITY_NOT_FOUND);
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new HEException(ACTIVITY_NOT_FOUND, activityId));


        Participation participation = new Participation(activity, volunteer, participationDto);
        participationRepository.save(participation);

        return new ParticipationDto(participation, User.Role.MEMBER);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ParticipationDto deleteParticipation(Integer participationId) {
        if (participationId == null) throw new HEException(PARTICIPATION_NOT_FOUND);
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new HEException(PARTICIPATION_NOT_FOUND, participationId));

        participation.delete();
        participationRepository.delete(participation);
        return new ParticipationDto(participation,  User.Role.VOLUNTEER);
    }
}
