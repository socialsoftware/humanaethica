describe('Volunteer', () => {
  beforeEach(() => {
    cy.demoMemberLogin()
  });

  afterEach(() => {
    cy.logout();
  });

  it('close', () => {
    cy.get('[data-cy="member"]').click();
  });
});
