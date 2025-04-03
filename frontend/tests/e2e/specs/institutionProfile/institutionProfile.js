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
      // open write assessment dialog
      cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
      .eq(0) // Pega a primeira linha
      .find('td .v-simple-checkbox')
      .click();
      // write review
    
      cy.get('[data-cy="saveInstitutionProfile"]').click();
      cy.wait('@createInstitutionProfile');
      cy.wait('@institutionProfile');
      
  
      cy.logout();
  
  
      
    });
  
});