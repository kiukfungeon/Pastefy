<script lang="ts" setup>
import Dialog from 'primevue/dialog'
import Checkbox from 'primevue/checkbox'
import Highlighted from '@/components/Highlighted.vue'
import { computed, type Ref, ref } from 'vue'

const props = defineProps<{
  pasteId: string
}>()

const visible = defineModel<boolean>('visible')

const origin = window.location.origin

const hideTitle = ref(false)
const hideActions = ref(false)
const addPadding = ref(false)
const settingsQuery = computed(() => {
  const query: Record<string, string> = {}
  if (hideTitle.value) query.hideTitle = 'true'
  if (hideActions.value) query.hideActions = 'true'
  if (addPadding.value) query.addPadding = 'true'

  return Object.keys(query).length ? '?' + new URLSearchParams(query).toString() : ''
})
const embedCode = computed(
  () => `<div id="pastefy"></div>
${'<'}script type="module">
  import { embedPastefy } from '${origin}/embed.js'
  embedPastefy(document.getElementById('pastefy'), 'P3fKIHq3'${
    origin !== 'https://pastefy.app'
      ? `, ${JSON.stringify({
          hideTitle: hideTitle.value || undefined,
          hideActions: hideActions.value || undefined,
        })}, "${origin}"`
      : ''
  })
</${'script>'}`,
)
const iframeEmbedCode = computed(
  () => `<iframe class="paste" src="${origin}/${props.pasteId}/embed${settingsQuery.value}" style="width: 700px; border: none; border-radius: 11px"></iframe>
