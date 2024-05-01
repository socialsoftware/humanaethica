package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private LocalDateTime acceptanceDate;
    private Integer rating;
    @ManyToOne
    private Activity activity;
    @ManyToOne
    private Volunteer volunteer;

    public Participation() {}

    public Participation(Activity activity, Volunteer volunteer, ParticipationDto participationDto) {
        setActivity(activity);
        setVolunteer(volunteer);
        setAcceptanceDate(LocalDateTime.now());
        setRating(participationDto.getRating());

        verifyInvariants();
    }

    public void update(ParticipationDto participationDto) {
        setRating(participationDto.getRating());

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public LocalDateTime getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        this.activity.addParticipation(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        this.volunteer.addParticipation(this);
    }

    private void verifyInvariants() {
        participateOnce();
        numberOfParticipantsLessOrEqualLimit();
        acceptanceAfterDeadline();
        ratingAfterEnd();
        ratingBetweenOneAndFive();
    }

    private void participateOnce() {
        if (this.activity.getParticipations().stream()
                .anyMatch(participation -> participation != this && participation.getVolunteer() == this.volunteer)) {
            throw new HEException(PARTICIPATION_VOLUNTEER_IS_ALREADY_PARTICIPATING);
        }
    }

    private void numberOfParticipantsLessOrEqualLimit() {
        if (this.activity.getNumberOfParticipatingVolunteers() > this.activity.getParticipantsNumberLimit()) {
            throw new HEException(PARTICIPATION_IS_FULL);
        }
    }

    private void acceptanceAfterDeadline() {
        if (this.acceptanceDate.isBefore(this.activity.getApplicationDeadline())) {
            throw new HEException(PARTICIPATION_ACCEPTANCE_BEFORE_DEADLINE);
        }
    }

    private void ratingAfterEnd() {
        if (this.rating != null && LocalDateTime.now().isBefore(activity.getEndingDate())) {
            throw new HEException(PARTICIPATION_RATING_BEFORE_END);
        }
    }

    private void ratingBetweenOneAndFive() {
        if (this.rating != null && (this.rating < 1 || this.rating > 5)) {
            throw new HEException(PARTICIPATION_RATING_BETWEEN_ONE_AND_FIVE, this.rating);
        }
    }

}
