describe('Enrollment', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForEnrollments()
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });

  it('create enrollment', () => {
    const MOTIVATION = 'I am very keen to help other people.';

    cy.intercept('POST', '/activities/1/enrollments').as('enroll');
    cy.intercept('GET', '/activities/1/enrollments').as('enrollments');


    // member login and check that there is activity with 0 enrollments
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 3)
      .eq(0)
      .children()
      .should('have.length', 13)
      .eq(3)
      .should('contain', 0)
    cy.logout();

    // volunteer login, creates enrollments
    cy.demoVolunteerLogin()
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="applyButton"]').click();
    cy.get('[data-cy="motivationInput"]').type(MOTIVATION);
    cy.get('[data-cy="saveEnrollment"]').click()
    cy.wait('@enroll')
    cy.logout()

    // member login and check that there is activity with 1 enrollment
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();

    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .children()
      .eq(3)
      .should('contain', 1)

    // open enrollments view for first activity
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="showEnrollments"]').click()
    cy.wait('@enrollments');
    // check that there are 1 enrollment
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 5)
      .eq(1)
      .should('contain', MOTIVATION)

    cy.logout();
  });
});