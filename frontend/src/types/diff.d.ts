declare module 'diff' {
  export function createTwoFilesPatch(
    oldFileName: string,
    newFileName: string,
    oldStr: string,
    newStr: string,
  ): string

  export function diffChars(
    oldStr: string,
    newStr: string,
  ): Array<{ value: string; added?: boolean; removed?: boolean; count?: number }>

