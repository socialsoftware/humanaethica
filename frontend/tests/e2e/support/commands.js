function toLocalIso(date) {
  const pad = (n) => n.toString().padStart(2, '0');

  const year = date.getFullYear();
  const month = pad(date.getMonth() + 1); // months are 0-based
  const day = pad(date.getDate());
  const hours = pad(date.getHours());
  const minutes = pad(date.getMinutes());

  return `${year}-${month}-${day}T${hours}:${minutes}`;
}

Cypress.Commands.add('typeDate', (selector, date) => {
  const isoLocal = toLocalIso(date);

  cy.get(selector)
    .find('input')
    .clear()
    .type(isoLocal)
    .blur();
});

