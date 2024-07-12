describe('Volunteer', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
    cy.createDemoEntities();
    cy.createDatabaseInfoForEnrollments()

    cy.demoMemberLogin()
  });

  afterEach(() => {
    cy.logout();
    cy.deleteAllButArs()
  });

  it('close', () => {
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="members"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="themes"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
  });

  it('suspend activity', () => {
    const JUSTIFICATION = 'This activity will be suspended for testing.';

    cy.intercept('GET', '/users/2/getInstitution').as('activities')

    // List activities.
    cy.get('[data-cy="institution"]').click()
    cy.get('[data-cy="activities"]').click()
    cy.wait('@activities')

    // Suspend first activity.
    cy.get('[data-cy="suspendButton"]').first().click()
    cy.get('[data-cy="suspensionReasonInput"]').type(JUSTIFICATION)
    cy.get('[data-cy="suspendActivity"]').click()

    // Check if first activity is suspended.
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 3)
      .should('contain', "SUSPENDED")
  });
});
