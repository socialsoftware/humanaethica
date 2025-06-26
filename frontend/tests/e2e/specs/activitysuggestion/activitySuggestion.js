describe('Activity Suggestion', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createDemoEntities();
    cy.createDatabaseInfoForActivitySuggestions();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });
  
  it('create activity suggestions', () => {
    const NAME = 'Digital Literacy Workshop';
    const REGION = 'Porto';
    const NUMBER = '4';
    const DESCRIPTION = 'A workshop to teach basic computer skills to seniors';
    const JUSTIFICATION = 'This activity suggestion will be rejected for testing.'

    cy.demoVolunteerLogin()
    // intercept create activity suggestion request and inject date values in the request body
    cy.intercept('POST', '/activitySuggestions/institution/1', (req) => {
      req.body = {
        applicationDeadline: '2025-07-11T17:00:00+00:00',
        startingDate: '2025-07-21T09:00:00+00:00',
        endingDate: '2025-07-23T17:00:00+00:00'
      };
    }).as('registerActivitySuggestion');
    // intercept get volunteer activity suggestions and get institutions
    cy.intercept('GET', '/activitySuggestions/volunteer/3').as('getVolunteerActivitySuggestions');
    cy.intercept('GET', '/institutions').as('getInstitutions');
    // go to volunteer activity suggestions view
    cy.get('[data-cy="volunteerActivitySuggestions"]').click();
    cy.get('[data-cy="my-suggestions"]').click();
    cy.wait('@getVolunteerActivitySuggestions');
    // check existing table entries
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .should('have.length', 2)
      .eq(0)
      .children()
      .should('have.length', 11)

    // go to create activity suggestion form
    cy.get('[data-cy="newActivitySuggestion"]').click();
    cy.wait('@getInstitutions');

    // fill form
    cy.get('[data-cy="suggestionNameInput"]').type(NAME);
    // select the institution
    cy.get('[data-cy="institutionNameDropdown"]').click();
    cy.get('.v-menu__content')
      .find('.v-list-item')
      .contains('DEMO INSTITUTION')
      .click();
    cy.get('[data-cy="suggestionRegionInput"]').type(REGION);
    cy.get('[data-cy="suggestionParticipantsNumberInput"]').type(NUMBER);
    cy.get('[data-cy="suggestionDescriptionInput"]').type(DESCRIPTION);
    // select dates   
    cy.get('#suggestionApplicationDeadlineInput-wrapper.date-time-picker')
      .find('.datepicker-day-text')
      .eq(10)
      .click({force: true});
    cy.get('#suggestionStartingDateInput-input').click();
    cy.get('#suggestionStartingDateInput-wrapper.date-time-picker')
      .find('.datepicker-day-text')
      .eq(20)
      .click({force: true});
    cy.get('#suggestionEndingDateInput-input').click();
    cy.get('#suggestionEndingDateInput-wrapper.date-time-picker')
      .find('.datepicker-day-text')
      .eq(22)
      .click({force: true});

    // save form
    cy.get('[data-cy="saveActivitySuggestion"]').click()
    // check request was done
    cy.wait('@registerActivitySuggestion')
    // check results
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .should('have.length', 3)
      .eq(0)
      .children()
      .should('have.length', 11)
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(0).should('contain', NAME)
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(1).should('contain', "DEMO INSTITUTION")
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(0).children().eq(4).should('contain', NUMBER)
    cy.logout();

    cy.demoMemberLogin()
    // intercept requests
    cy.intercept('GET', '/users/2/getInstitution').as('getInstitution');
    cy.intercept('GET', '/activitySuggestions/institution/1').as('getActivitySuggestions');
    // go to institution activity suggestions view
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activitysuggestions"]').click();
    // wait for request to be done
    cy.wait('@getInstitution');
    cy.wait('@getActivitySuggestions');
    // check first activity suggestion is in review
    cy.get('[data-cy="memberActivitySuggestionsTable"] tbody tr')
      .should('have.length', 3)
      .eq(0).children().eq(9).should('contain', "IN_REVIEW")
    // Approve first activity suggestion
    cy.get('[data-cy="approveActivitySuggestionButton"]').first().click()
    cy.logout();

    cy.demoVolunteerLogin();
    // intercept get volunteer activity suggestions and get institutions
    cy.intercept('GET', '/activitySuggestions/volunteer/3').as('getVolunteerActivitySuggestions');
    cy.intercept('GET', '/institutions').as('getInstitutions');
    // go to volunteer activity suggestions view
    cy.get('[data-cy="volunteerActivitySuggestions"]').click();
    cy.get('[data-cy="my-suggestions"]').click();
    cy.wait('@getVolunteerActivitySuggestions');
    // Check if the activity suggestion is approved
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(2).children().eq(9).should('contain', "APPROVED")

    cy.logout();

    cy.demoMemberLogin()
    // intercept requests
    cy.intercept('GET', '/users/2/getInstitution').as('getInstitution');
    cy.intercept('GET', '/activitySuggestions/institution/1').as('getActivitySuggestions');
    // go to institution activity suggestions view
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activitysuggestions"]').click();
    // wait for request to be done
    cy.wait('@getInstitution');
    cy.wait('@getActivitySuggestions');
    // Reject first activity suggestion
    cy.get('[data-cy="rejectActivitySuggestionButton"]').first().click()
    cy.get('[data-cy="rejectionReasonInput"]').type(JUSTIFICATION)
    cy.get('[data-cy="rejectActivitySuggestion"]').click()

    cy.logout();

    cy.demoVolunteerLogin();
    // intercept get volunteer activity suggestions and get institutions
    cy.intercept('GET', '/activitySuggestions/volunteer/3').as('getVolunteerActivitySuggestions');
    cy.intercept('GET', '/institutions').as('getInstitutions');
    // go to volunteer activity suggestions view
    cy.get('[data-cy="volunteerActivitySuggestions"]').click();
    cy.get('[data-cy="my-suggestions"]').click();
    cy.wait('@getVolunteerActivitySuggestions');
    // Check if the activity suggestion is rejected
    cy.get('[data-cy="volunteerActivitySuggestionsTable"] tbody tr')
      .eq(2).children().eq(9).should('contain', "REJECTED")

    cy.logout();
  });
});
