<p align="center">
    <a>
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="/.github/logo-white.svg">
        <source media="(prefers-color-scheme: light)" srcset="/.github/logo-black.svg" />
        <img src="/.github/logo-black.svg" width="200px" />
    </picture>
    </a>
</p>


<img src="./.github/screenshots/paste.png" width="100%" >

Pastefy is an open source alternative to Gists or Pastebin.<br>
You want to share some code to your friends or just save it for yourself? Just paste it.

Try out the public instance https://pastefy.app <br>
Visit the docs for everything around Pastefy here: https://docs.pastefy.app

## Features
- Raw-Preview
- Copy Button
- Fork
- An API ([Docs](https://docs.pastefy.app/api/))
  - [Javascript/Typescript](https://github.com/interaapps/js-api-clients?tab=readme-ov-file#pastefy)
  - [Java](https://github.com/interaapps/pastefy-java-apiclient)
  - [Go](https://github.com/interaapps/pastefy-go-api)
- Log in
  - Folders 
  - See your created pastes
  - delete created pastes
- Create paste with `curl -F f=@file.txt pastefy.app`
- Extensions:
  - VS-Code
  - Raycast
- Previews for (file extension)
  - **[See examples here](https://pastefy.app/EByZpNoS)**
  - Markdown (.md)
  - Mermaid (.mermaid or .mmd)
  - SVG (.svg)
  - CSV (.csv)
  - GeoJSON (.geojson)
  - Diff (.diff)
  - calendar (.ics)
  - regex (.regex)
  - [Asciinema recordings](https://docs.pastefy.app/features/integrations/asciinema.html) (.cast) [(Example)](https://pastefy.app/ar9ehz8w)

# Overview
- [Screenshots](#Screenshots)
- Deploy
  - [Docker](https://docs.pastefy.app/self-hosting/installation/docker.html)
  - [Docker-Compose](https://docs.pastefy.app/self-hosting/installation/docker-compose.html)
  - [Container-Less](#Container-Less)
- [Configuration](https://docs.pastefy.app/self-hosting/configuration.html)
- [Develop](#Develop)
- [API](https://docs.pastefy.app/api/)

# Screenshots
<img src="./.github/screenshots/home.png" width="46%" >
<img src="./.github/screenshots/paste.png" width="46%" >
<img src="./.github/screenshots/new.png" width="46%" >
<img src="./.github/screenshots/fullscreen.png" width="46%" >

## Deploy
