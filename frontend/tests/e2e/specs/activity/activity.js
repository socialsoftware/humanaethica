describe('Activity', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });
  
  it('create activities', () => {
    const NAME = 'Elderly Assistance';
    const REGION = 'Lisbon';
    const NUMBER = '3';
    const DESCRIPTION = 'Play card games with elderly over 80';

    cy.demoMemberLogin()
    // intercept create activity request and inject date values in the request body
    cy.intercept('POST', '/activities', (req) => {
      req.body = {
        applicationDeadline: '2024-01-13T12:00:00+00:00',
        startingDate: '2024-01-14T12:00:00+00:00',
        endingDate: '2024-01-15T12:00:00+00:00'
      };
    }).as('register');
    // intercept get institutions
    cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
    cy.intercept('GET', '/themes/availableThemes').as('availableTeams')
    // go to create activity form
    cy.get('[data-cy="institution"]').click();

    cy.get('[data-cy="activities"]').click();
    cy.wait('@getInstitutions');

    cy.get('[data-cy="newActivity"]').click();
    cy.wait('@availableTeams');

    // fill form
    cy.get('[data-cy="nameInput"]').type(NAME);
    cy.get('[data-cy="regionInput"]').type(REGION);
    cy.get('[data-cy="participantsNumberInput"]').type(NUMBER);
    cy.get('[data-cy="descriptionInput"]').type(DESCRIPTION);
    cy.get('#applicationDeadlineInput-input').click();
    cy.selectDateTimePickerDate();
    cy.get('#startingDateInput-input').click();
    cy.selectDateTimePickerDate();
    cy.get('#endingDateInput-input').click();
    cy.selectDateTimePickerDate();
    // save form
    cy.get('[data-cy="saveActivity"]').click()
    // check request was done
    cy.wait('@register')
    // check results
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 13)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(0).should('contain', NAME)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(1).should('contain', REGION)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(2).should('contain', NUMBER)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(6).should('contain', DESCRIPTION);
    cy.logout();

    cy.demoVolunteerLogin();
    // intercept requests
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('GET', '/assessments/volunteer').as('getVolunteerAssessments');
    cy.intercept('GET', '/participations/volunteer').as('getVolunteerParticipations');
    cy.intercept('GET', '/enrollments/volunteer').as('getVolunteerEnrollments');
    // go to volunteer activities view
    cy.get('[data-cy="volunteerActivities"]').click();
    // check request was done
    cy.wait('@getActivities');
    cy.wait('@getVolunteerEnrollments');
    cy.wait('@getVolunteerParticipations');
    cy.wait('@getVolunteerAssessments');
    // check results
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 11)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .eq(0).children().eq(0).should('contain', NAME)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .eq(0).children().eq(1).should('contain', REGION)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .eq(0).children().eq(3).should('contain', NUMBER)
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .eq(0).children().eq(5).should('contain', DESCRIPTION);
    cy.logout();

    cy.demoAdminLogin();
    // intercept get requests
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('GET', '/themes').as('getThemes');
    cy.intercept('GET', '/institutions').as('getInstitutions');
    // go to admin activities view
    cy.get('[data-cy="admin"]').click();
    cy.get('[data-cy="adminActivities"]').click();
    // check requests were done
    cy.wait('@getActivities');
    cy.wait('@getThemes');
    cy.wait('@getInstitutions');
    cy.wait(1000);
    // check results
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 14)
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .eq(0).children().eq(1).should('contain', NAME)
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .eq(0).children().eq(2).should('contain', REGION)
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .eq(0).children().eq(3).should('contain', NUMBER)
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .eq(0).children().eq(4).should('contain', DESCRIPTION);
    cy.logout();
  });
});
