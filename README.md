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
