describe('InstitutionProfile', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForInstitutionProfile();
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });

  it('create institution profile', () => {
    const SHORT_DESCRIPTION = 'This is a short description.';

    cy.intercept('POST', '/profile/institution').as('createInstitutionProfile');

    cy.intercept('GET', '/profile/institution/1').as('institutionProfile');
    cy.intercept('GET', '/institutions/1/assessments').as('assessments');
    cy.intercept('GET', '/users/*/getInstitution').as('getInstitution');
    cy.intercept('GET', '/profile/institutions/views').as('institutionProfileList');

    // member creates institution profile
    cy.demoMemberLogin();
    cy.get('[data-cy="profiles"]').click();
    cy.get('[data-cy="member-profile"]').click();
    cy.wait('@getInstitution');
    cy.wait('@assessments');

    cy.get('[data-cy="createInstitutionPButton"]').click();

    cy.get('[data-cy="shortDescriptionInput"]').type(SHORT_DESCRIPTION);
    // verify that assessment with review "muito bom" is in row 0
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .eq(2)
      .should('contain', "Participation numa cãorrida da Demo Institution. O evento foi muito bem organizado, parabéns!")
    // open write assessment dialog
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
    .eq(0) // Pega a primeira linha
    .find('td .v-simple-checkbox')
    .click();
    // write review
  
    cy.get('[data-cy="saveInstitutionProfile"]').click();
    cy.wait('@createInstitutionProfile');
    cy.wait('@institutionProfile');
    cy.get('[data-cy="numAssessments"]') 
    .should('contain', '2');
    cy.get('[data-cy="institutionProfileAssessmentsTable"] tbody tr')
    .should('have.length', 1)
    .eq(0)
    .children()
    .eq(1)
    .should('contain', "Participation numa cãorrida da Demo Institution. O evento foi muito bem organizado, parabéns!");

    cy.logout();

    //non authenticated user goes to institution profiles list
    cy.get('[data-cy="profiles"]').click();
    cy.get('[data-cy="view-profiles"]').click();
    cy.wait('@institutionProfileList');

    // verify that assessment has "DEMO INSTITUTION-2" is in row 0
    cy.get('[data-cy="institutionProfilesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 5)
      .eq(0)
      .should('contain', "DEMO INSTITUTION")

    
  });

});