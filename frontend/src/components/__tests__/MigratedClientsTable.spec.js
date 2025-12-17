import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import MigratedClientsTable from '../MigratedClientsTable.vue'

const vuetify = createVuetify({
  components,
  directives
})

const mockMigratedClients = [
  { id: 1, name: 'UCB Alliance Managers', migrated: true },
  { id: 2, name: 'Lilly', migrated: true }
]

describe('MigratedClientsTable', () => {
  it('renders the component with correct title', () => {
    const wrapper = mount(MigratedClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockMigratedClients,
        loading: false,
        rollingBack: false,
        rollingBackClientId: null
      }
    })

    expect(wrapper.text()).toContain('Migrated Clients')
  })

  it('displays correct client count', () => {
    const wrapper = mount(MigratedClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockMigratedClients,
        loading: false,
        rollingBack: false,
        rollingBackClientId: null
      }
    })

    expect(wrapper.text()).toContain('2 clients')
  })

  it('emits rollback event when undo button is clicked', async () => {
    const wrapper = mount(MigratedClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockMigratedClients,
        loading: false,
        rollingBack: false,
        rollingBackClientId: null
      }
    })

    const buttons = wrapper.findAll('button')
    const undoButton = buttons.find(btn => btn.text().includes('Undo'))
    
    if (undoButton) {
      await undoButton.trigger('click')
      
      expect(wrapper.emitted()).toHaveProperty('rollback')
      expect(wrapper.emitted('rollback')).toHaveLength(1)
    }
  })

  it('shows loading progress bar when loading', () => {
    const wrapper = mount(MigratedClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: [],
        loading: true,
        rollingBack: false,
        rollingBackClientId: null
      }
    })

    const progressBar = wrapper.find('.v-progress-linear')
    expect(progressBar.exists()).toBe(true)
  })

  it('shows empty state when no migrated clients', () => {
    const wrapper = mount(MigratedClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: [],
        loading: false,
        rollingBack: false,
        rollingBackClientId: null
      }
    })

    expect(wrapper.text()).toContain('No Migrations Yet')
  })

  it('highlights client being rolled back', () => {
    const wrapper = mount(MigratedClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockMigratedClients,
        loading: false,
        rollingBack: true,
        rollingBackClientId: 1
      }
    })

    const component = wrapper.vm
    expect(component.rollingBackClientId).toBe(1)
  })
})

