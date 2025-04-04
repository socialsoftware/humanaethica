describe('VolunteerProfile', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
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

      cy.intercept('POST', '/profile/volunteer').as('registerVolunteerProfile');
      
      // intercept get participations
      cy.intercept('GET', '/participations/volunteer').as('getParticipations');
      cy.intercept('GET', '/activities').as('getActivities');
      cy.intercept('GET', '/profile/volunteer/3').as('getVolunteerProfile');

      // go to create volunteer profile form
      cy.get('[data-cy="profiles"]').click();
      cy.get('[data-cy="volunteer-profile"]').click();

      cy.wait('@getActivities');
      cy.wait('@getParticipations');

      
      cy.get('[data-cy="createVolunteerProfile"]').click({ force: true });

      // fill form
      cy.get('[data-cy="shortBioInput"]').type(SHORT_BIO);

      cy.get('[data-cy="participationsTable"] tbody tr').eq(1).find('td .v-simple-checkbox').click({ force: true });

      // check form
      cy.get('[data-cy="participationsTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .should('have.length', 6)


      // save form
      cy.get('[data-cy="saveVolunteerProfile"]').click();

      cy.wait('@registerVolunteerProfile')
      cy.wait('@getVolunteerProfile');

      // check data in volunteer profile
      cy.get('[data-cy="selectedParticipationsTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .children()
      .should('have.length', 4)

      cy.get('[data-cy="selectedParticipationsTable"] tbody tr')
        .eq(0).children().eq(0).should('contain', ACTIVITY_NAME)

      // check number of total participations
      cy.get('[data-cy="NumTotalParticipations"] span') // Select the span inside the div
      .should('have.text', '2'); // Check that the text is '4'
      

      cy.logout();


      cy.intercept('GET', '/profile/volunteer/views').as('getProfilesList');

      cy.get('[data-cy="profiles"]').click();
      cy.get('[data-cy="view-profiles"]').click();

      cy.wait('@getProfilesList');

      // Check that the table exists and has the profile created
      cy.get('[data-cy="volunteerProfilesTable"]').should('be.visible');
      cy.get('[data-cy="institutionProfilesTable"]').should('be.visible');

      cy.get('[data-cy="volunteerProfilesTable"]')
      .should('exist')
      .and('not.be.empty');

      cy.get('[data-cy="volunteerProfilesTable"]')
      .find('tr').should('have.length.greaterThan', 0)

      cy.get('[data-cy="volunteerProfilesTable"] tbody tr')
      .eq(0).children().eq(0).should('contain.text', NAME);

      cy.get('[data-cy="volunteerProfilesTable"] tbody tr')
      .eq(0).children().eq(1).should('contain.text', SHORT_BIO);
  })
    
  it('close', () => {

  });
});