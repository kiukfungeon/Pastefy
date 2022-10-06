import fs from 'fs'

let outTop = 'import {hljs} from "@/utils/highlight"'
let outBottom = ''

const ignoredLangs = ['mathematica', 'gml', '1c', 'isbl', 'pgsql', 'maxima', 'sqf']

let i = 0

function addLang(lang, langFile = undefined) {
  const langVar = `lang${i++}`
  outTop += `\nimport ${langVar} from 'highlight.js/lib/languages/${langFile || lang}'`
