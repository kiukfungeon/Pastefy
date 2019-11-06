export function embedPastefy(parentEl, pasteId, settings = {}, origin = 'https://pastefy.app') {
  if (typeof settings === 'string') origin = settings

