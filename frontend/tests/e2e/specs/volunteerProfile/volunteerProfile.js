describe('VolunteerProfile', () => {
  beforeEach(() => {
    cy.createDemoEntities();
    cy.createParticipationsForVolunteerProfile()
  });

  afterEach(() => {
      cy.deleteAllButArs();
  });

  it('create volunteerProfile', () => {
      const SHORT_BIO = 'This is an example of a short bio';
      const ACTIVITY_NAME = 'Latidos do Bem';
      const NAME = 'DEMO-VOLUNTEER';

      //Login as a volunteer
      cy.demoVolunteerLogin()

      cy.intercept('POST', '/profile/volunteer').as('register');
      
      // intercept get participations
      cy.intercept('GET', '/participations/volunteer').as('getParticipations');
      cy.intercept('GET', '/activities').as('getActivities');
      cy.intercept('GET', '/profile/volunteer/3').as('getVolunteerProfile');

      // go to create volunteer profile form
      cy.get('[data-cy="profiles"]').click();
      cy.get('[data-cy="volunteer-profile"]').click();

      cy.wait('@getActivities');
      cy.wait('@getParticipations');
      cy.wait('@getVolunteerProfile');
      
      cy.get('[data-cy="createVolunteerProfile"]').click({ force: true });

      // fill form
      cy.get('[data-cy="shortBioInput"]').type(SHORT_BIO);

      cy.get('[data-cy="participationsTable"] tbody tr').eq(1).find('td .v-simple-checkbox').click({ force: true });

      // save form
      cy.get('[data-cy="saveVolunteerProfile"]').click();

      cy.wait('@register')

      cy.logout();

  })
    
  it('close', () => {

  });
});