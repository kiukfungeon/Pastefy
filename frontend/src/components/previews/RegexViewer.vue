<template>
  <div>
    <Highlighted
      :contents="regex"
      lang="regex"
      class="border-b-1 border-neutral-200 dark:border-neutral-700"
    />
    <div class="flex w-full flex-col gap-5 p-5">
      <div class="flex flex-col gap-3">
        <div class="flex items-center justify-between gap-2">
          <label class="text-lg font-semibold text-neutral-700 dark:text-neutral-300">
            {{ $t('previews.regexViewer.testInput') }}
          </label>
          <InputText v-model="flags" :placeholder="$t('previews.regexViewer.flags')" size="small" class="w-[5rem]" />
        </div>
        <div class="relative">
          <div
            class="pointer-events-none absolute h-full w-full px-(--p-textarea-padding-x) py-(--p-textarea-padding-y) whitespace-pre-wrap"
            v-html="highlightedResult || '&nbsp;'"
          />
          <Textarea
            v-model="testInput"
            fluid
            :placeholder="$t('previews.regexViewer.insertYourTestStringHere')"
