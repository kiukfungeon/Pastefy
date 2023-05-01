<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

type Entry = {
  key: string
  value: string
}

const props = withDefaults(
  defineProps<{
    label: string
    keyPlaceholder?: string
    valuePlaceholder?: string
    addLabel?: string
  }>(),
  {
    addLabel: 'add entry',
  },
)

const model = defineModel<Entry[]>({ required: true })

const addItem = () => {
  model.value = [...model.value, { key: '', value: '' }]
}

const removeItem = (index: number) => {
  model.value = model.value.filter((_, currentIndex) => currentIndex !== index)
}

const updateItem = (index: number, next: Partial<Entry>) => {
  model.value = model.value.map((entry, currentIndex) =>
    currentIndex === index ? { ...entry, ...next } : entry,
  )
}
</script>
