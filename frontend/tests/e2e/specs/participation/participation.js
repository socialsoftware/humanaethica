describe('Participation', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForParticipations()
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });

  it('create participation', () => {
    const MEMBER_REVIEW_1 = 'The volunteer did a good job';

    cy.intercept('GET', '/activities/1/enrollments').as('enrollments');
    cy.intercept('POST', '/activities/1/participations').as('participation');

    // member login and check that there are 2 activities with 2 enrollments
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .eq(3)
      .should('contain', 2)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(1)
      .children()
      .eq(3)
      .should('contain', 2)

    // open enrollments view for first activity
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="showEnrollments"]').click()
    cy.wait('@enrollments');
    // check that there are 2 enrollments
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .should('have.length', 7)
      .eq(4)
      .should('contain', 'false')

    // open create participation dialog
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="selectParticipantButton"]')
      .click();
    // write ranking
    cy.get('[data-cy="participantsNumberInput"]').type(3);
    // write review
    cy.get('[data-cy="participantsReviewInput"]').type(MEMBER_REVIEW_1);
    // create participation
    cy.get('[data-cy="createParticipation"]').click();
    cy.wait('@participation');
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', 'true')

    // return to activities view
    cy.get('[data-cy="getActivities"]').click();
    // check that there is 2 participations
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', '2')
    
    cy.logout();

  });

  it('update participation', () => {
    const MEMBER_REVIEW_1 = 'The volunteer did an okay job';
    const MEMBER_REVIEW_2 = 'The volunteer did a good job';
    const VOLUNTEER_REVIEW = 'The activity was well organized';

  cy.intercept('GET', '/activities/1/enrollments').as('enrollments');
  cy.intercept('POST', '/activities/1/participations').as('participation');


    // member login and check that there are 2 activities with 2 enrollments
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .eq(3)
      .should('contain', 2)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(1)
      .children()
      .eq(3)
      .should('contain', 2)

    // open enrollments view for first activity
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="showEnrollments"]').click()
    cy.wait('@enrollments');
    // check that there are 2 enrollments
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .should('have.length', 7)
      .eq(4)
      .should('contain', 'false')

    // open create participation dialog
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="selectParticipantButton"]')
      .click();
    // write ranking
    cy.get('[data-cy="participantsNumberInput"]').type(3);
    //write review
    cy.get('[data-cy="participantsReviewInput"]').type(MEMBER_REVIEW_1);
    // create participation
    cy.get('[data-cy="createParticipation"]').click();
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', 'true')


    // Check if the review exists
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(2)
      .invoke('text')
      .should('include', 'The volunteer did an okay job')
      .and('include', 'Rating: ')
      .and('match', /★{3}☆{2} 3\/5/);



    // open edit participation dialog
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="editParticipantButton"]')
      .click();
    // edit ranking
    cy.get('[data-cy="participantsNumberInput"]').clear();
    cy.get('[data-cy="participantsNumberInput"]').type(5);
    cy.get('[data-cy="participantsReviewInput"]').clear();
    cy.get('[data-cy="participantsReviewInput"]').type(MEMBER_REVIEW_2);
    // edit participation
    cy.get('[data-cy="createParticipation"]').click();;
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', 'true')

    // Check if the review exists
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(2)  // Assuming this is the correct index for the rating column
      .invoke('text')
      .should('include', 'The volunteer did a good job')
      .and('include', 'Rating: ')
      .and('match', /★{5} 5\/5/);


    // verify participation status
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', 'true')

    // return to activities view
    cy.get('[data-cy="getActivities"]').click();
    // check that there is 2 participations
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', '2')

    cy.logout();

    cy.demoVolunteerLogin()
    cy.get('[data-cy="volunteerEnrollments"]').click();
    cy.get('[data-cy="volunteerEnrollmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="writeParticipationButton"]').click();

    // write ranking
    cy.get('[data-cy="ratingInput"]').type(3);
    //write review
    cy.get('[data-cy="reviewInput"]').type(VOLUNTEER_REVIEW);
    cy.get('[data-cy="saveParticipation"]').click();
    cy.wait(1)

    // Check if rating was updated
    cy.get('[data-cy="volunteerEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(6)  // Assuming this is the correct index for the rating column
      .invoke('text')
      .should('include', 'The volunteer did a good job')
      .and('include', 'Rating: ')
      .and('match', /★{5} 5\/5/);


    // Verify the member review
    cy.get('[data-cy="volunteerEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(7)
      .invoke('text')
      .should('include', 'The activity was well organized')
      .and('include', 'Rating: ')
      .and('match', /★{3}☆{2} 3\/5/);


    cy.logout();

    // member login and check that there are 2 activities with 2 enrollments
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .eq(3)
      .should('contain', 2)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(1)
      .children()
      .eq(3)
      .should('contain', 2)

    // open enrollments view for first activity
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="showEnrollments"]').click()
    cy.wait('@enrollments');

    // Check if rating was updated
    // Update the assertion to match the new format with stars
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(2)  // Assuming this is the correct index for the rating column
      .invoke('text')
      .should('include', 'The volunteer did a good job')
      .and('include', 'Rating: ')
      .and('match', /★{5} 5\/5/);


    // Verify the member review
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(3)
      .invoke('text')
      .should('include', 'The activity was well organized')
      .and('include', 'Rating: ')
      .and('match', /★{3}☆{2} 3\/5/);


    cy.logout();

  });

  it('delete participation', () => {
    const MEMBER_REVIEW_1 = 'The volunteer did an okay job';

    cy.intercept('GET', '/activities/1/enrollments').as('enrollments');
    cy.intercept('POST', '/activities/1/participations').as('participation');

    // member login and check that there are 2 activities with 2 enrollments
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .eq(3)
      .should('contain', 2)
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(1)
      .children()
      .eq(3)
      .should('contain', 2)

    // open enrollments view for first activity
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="showEnrollments"]').click()
    cy.wait('@enrollments');
    // check that there are 2 enrollments
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .should('have.length', 7)
      .eq(4)
      .should('contain', 'false')

    // create a participation
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="selectParticipantButton"]')
      .click();

    // write ranking
    cy.get('[data-cy="participantsNumberInput"]').type(3);
    // write review
    cy.get('[data-cy="participantsReviewInput"]').type(MEMBER_REVIEW_1);

    cy.get('[data-cy="createParticipation"]').click();
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', 'true')

    // delete a participation
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="deleteParticipantButton"]').click()
    cy.get('[data-cy="deleteParticipationDialogButton"]').click();


    // Verify that the participation status is now false
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4)
      .should('contain', 'false');

    // return to activities view
    cy.get('[data-cy="getActivities"]').click();
    // check that there is 2 participations
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(4).should('contain', '1')
    cy.logout();

  });


});