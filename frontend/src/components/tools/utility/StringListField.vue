<script setup lang="ts">
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

const props = withDefaults(
  defineProps<{
    label: string
    placeholder?: string
    addLabel?: string
  }>(),
  {
    addLabel: 'add entry',
  },
)

const model = defineModel<string[]>({ required: true })

const addItem = () => {
  model.value = [...model.value, '']
}

const removeItem = (index: number) => {
  model.value = model.value.filter((_, currentIndex) => currentIndex !== index)
}
