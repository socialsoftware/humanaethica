describe('Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForAssessments()
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });

  it('create assessment', () => {
    const REVIEW = 'The institution has provided all the information.';

    cy.intercept('GET', '/enrollments/volunteer').as('enrollments');
    cy.intercept('GET', '/participations/volunteer').as('participations');
    cy.intercept('GET', '/assessments/volunteer').as('assessments');
    cy.intercept('POST', '/institutions/1/assessments').as('review');
    cy.intercept('GET', '/institutions/1/assessments').as('reviews');

    // volunteer writes assessment
    cy.demoVolunteerLogin();
    cy.get('[data-cy="volunteerActivities"]').click();
    cy.wait('@enrollments');
    cy.wait('@participations');
    cy.wait('@assessments');
    // verify that activity A1 is in row 0
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .should('have.length', 6)
      .eq(0)
      .children()
      .eq(0)
      .should('contain', "A1")
    // open write assessment dialog
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="writeAssessmentButton"]')
      .click();
    // write review
    cy.get('[data-cy="reviewInput"]').type(REVIEW);
    cy.get('[data-cy="saveAssessment"]').click();
    cy.wait('@review');
    cy.logout();

    // member check review
    cy.demoMemberLogin();
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="assessments"]').click();
    cy.wait('@reviews');
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 3)
      .eq(0).should('contain', REVIEW);
    cy.logout();

  });
});
