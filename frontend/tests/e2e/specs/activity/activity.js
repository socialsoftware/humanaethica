describe('Activity', () => {
  beforeEach(() => {
    cy.deleteAllButArs()
  });

  afterEach(() => {
    cy.deleteAllButArs()
  });

  it('create activities', () => {
    const NAME = 'Elderly Assistance';
    const REGION = 'Lisbon';
    const NUMBER = '3';
    const DESCRIPTION = 'Play card games with elderly over 80';

    // intercept create activity request and inject date values in the request body
    cy.intercept('POST', '/activities', (req) => {
      req.body = {
        applicationDeadline: '2024-01-13T12:00:00+00:00',
        startingDate: '2024-01-14T12:00:00+00:00',
        endingDate: '2024-01-15T12:00:00+00:00'
      };
    }).as('register');

    cy.demoMemberLogin()
    // go to create activity form
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="newActivity"]').click();
    // fill form
    cy.get('[data-cy="nameInput"]').type(NAME);
    cy.get('[data-cy="regionInput"]').type(REGION);
    cy.get('[data-cy="participantsNumberInput"]').type(NUMBER);
    cy.get('[data-cy="descriptionInput"]').type(DESCRIPTION);
    cy.get('#applicationDeadlineInput-input').click();
    cy.selectDateTimePickerDate();
    cy.get('#startingDateInput-input').click();
    cy.selectDateTimePickerDate();
    cy.get('#endingDateInput-input').click();
    cy.selectDateTimePickerDate();
    // save form
    cy.get('[data-cy="saveActivity"]').click()
    // check request was done
    cy.wait('@register')
    // check results
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .then(($row) => {
        cy.wrap($row).children()
          .should('have.length', 11)
          .each(($column, columnIndex) => {
            if (columnIndex === 0) {
              cy.wrap($column).invoke('text').should('equal', NAME);
            } else if (columnIndex === 1) {
              cy.wrap($column).invoke('text').should('equal', REGION);
            } else if (columnIndex === 2) {
              cy.wrap($column).invoke('text').should('equal', NUMBER);
            } else if (columnIndex === 4) {
              cy.wrap($column).invoke('text').should('equal', DESCRIPTION);
            }
          });
      })
    cy.logout();

    cy.demoVolunteerLogin();
    // intercept get activities request
    cy.intercept('GET', '/activities').as('getActivities');
    // go to volunteer activities view
    cy.get('[data-cy="volunteerActivities"]').click();
    // check request was done
    cy.wait('@getActivities');
    // check results
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .then(($row) => {
        cy.wrap($row).children()
          .should('have.length', 10)
          .each(($column, columnIndex) => {
            if (columnIndex === 0) {
              cy.wrap($column).invoke('text').should('equal', NAME);
            } else if (columnIndex === 1) {
              cy.wrap($column).invoke('text').should('equal', REGION);
            } else if (columnIndex === 2) {
              cy.wrap($column).invoke('text').should('equal', NUMBER);
            } else if (columnIndex === 4) {
              cy.wrap($column).invoke('text').should('equal', DESCRIPTION);
            }
          });
      })
    cy.logout();

    cy.demoAdminLogin();
    // intercept get requests
    cy.intercept('GET', '/activities').as('getActivities');
    cy.intercept('GET', '/themes').as('getThemes');
    cy.intercept('GET', '/institutions').as('getInstitutions');
    // go to admin activities view
    cy.get('[data-cy="admin"]').click();
    cy.get('[data-cy="adminActivities"]').click();
    // check requests were done
    cy.wait('@getActivities');
    cy.wait('@getThemes');
    cy.wait('@getInstitutions');
    cy.wait(1000);
    // check results
    cy.get('[data-cy="adminActivitiesTable"] tbody tr')
      .should('have.length', 1)
      .eq(0)
      .then(($row) => {
        cy.wrap($row).children()
          .should('have.length', 14)
          .each(($column, columnIndex) => {
            if (columnIndex === 1) {
              cy.wrap($column).invoke('text').should('equal', NAME);
            } else if (columnIndex === 2) {
              cy.wrap($column).invoke('text').should('equal', REGION);
            } else if (columnIndex === 3) {
              cy.wrap($column).invoke('text').should('equal', NUMBER);
            } else if (columnIndex === 4) {
              cy.wrap($column).invoke('text').should('equal', DESCRIPTION);
            }
          });
      })
    cy.logout();
  });
});
