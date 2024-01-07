describe('Volunteer', () => {
  beforeEach(() => {
    cy.demoMemberLogin()
  });

  afterEach(() => {
    cy.logout();
  });

  it('close', () => {
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="members"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="themes"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
  });
});
