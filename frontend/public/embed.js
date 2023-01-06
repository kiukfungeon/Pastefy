export function embedPastefy(parentEl, pasteId, settings = {}, origin = 'https://pastefy.app') {
  if (typeof settings === 'string') origin = settings

  const iframe = document.createElement('iframe')

  iframe.src = `${origin}/${pasteId}/embed?${new URLSearchParams(settings).toString()}`

  if (iframe) {
    iframe.onload = () => {
      window.addEventListener('message', (e) => {
        if (e.data && 'height' in e.data) {
