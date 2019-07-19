export type Plugin = {
  init?: () => void
}

export const plugins = new Map<string, Plugin>()

