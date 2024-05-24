describe('Edit Assessment', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForAssessments();
    cy.createDatabaseInfoForVolunteerAssessments();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('edit assessment', () => {
    const UPDATE = "This is an update.";

    cy.intercept('GET', '/assessments/volunteer').as('assessments');
    cy.intercept('PUT', '/assessments/1').as('review');

    // volunteer updates assessment
    cy.demoVolunteerLogin();
    cy.get('[data-cy="volunteerAssessments"]').click();
    cy.wait('@assessments');

    // verify that assessment has "DEMO INSTITUTION-2" is in row 0
    cy.get('[data-cy="volunteerAssessmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .eq(0)
      .should('contain', "DEMO INSTITUTION-2")

    // open edit assessment dialog
    cy.get('[data-cy="volunteerAssessmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="writeAssessmentButton"]')
      .click();

    // edit review
    cy.get('[data-cy="reviewInput"]').clear().type(UPDATE);
    cy.get('[data-cy="saveAssessment"]').click();
    cy.wait('@review');

    // check review
    cy.get('[data-cy="volunteerAssessmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .eq(1)
      .should('contain', UPDATE);

    cy.logout();
  });

  it('delete assessment', () => {
    cy.intercept('GET', '/assessments/volunteer').as('assessments');
    cy.intercept('DELETE', '/assessments/1').as('delete');

    // volunteer updates assessment
    cy.demoVolunteerLogin();
    cy.get('[data-cy="volunteerAssessments"]').click();
    cy.wait('@assessments');

    // verify that assessment has "DEMO INSTITUTION-2" is in row 0
    cy.get('[data-cy="volunteerAssessmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .eq(0)
      .should('contain', "DEMO INSTITUTION-2")

    // delete assessment
    cy.get('[data-cy="volunteerAssessmentsTable"] tbody tr')
      .eq(0)
      .find('[data-cy="deleteAssessmentButton"]')
      .click();
    cy.wait('@delete');

    // check review
    cy.get('[data-cy="volunteerAssessmentsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .eq(0)
      .should('contain', 'No data available');

    cy.logout();
  });
});
