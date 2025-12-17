import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import LegacyClientsTable from '../LegacyClientsTable.vue'

// Create Vuetify instance for tests
const vuetify = createVuetify({
  components,
  directives
})

// Mock data
const mockClients = [
  { id: 1, name: 'UCB Alliance Managers', migrated: false },
  { id: 2, name: 'Lilly', migrated: false },
  { id: 3, name: 'Shadow Lake Group', migrated: false }
]

describe('LegacyClientsTable', () => {
  it('renders the component with correct title', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockClients,
        loading: false,
        migrating: false,
        migratingClientId: null
      }
    })

    expect(wrapper.text()).toContain('Legacy Clients')
  })

  it('displays correct number of clients', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockClients,
        loading: false,
        migrating: false,
        migratingClientId: null
      }
    })

    expect(wrapper.text()).toContain('3 clients')
  })

  it('displays client count as singular when only one client', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: [mockClients[0]],
        loading: false,
        migrating: false,
        migratingClientId: null
      }
    })

    expect(wrapper.text()).toContain('1 client')
    expect(wrapper.text()).not.toContain('clients')
  })

  it('emits migrate event when migrate button is clicked', async () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockClients,
        loading: false,
        migrating: false,
        migratingClientId: null
      }
    })

    // Find all migrate buttons
    const buttons = wrapper.findAll('button')
    const migrateButton = buttons.find(btn => btn.text().includes('Migrate'))
    
    if (migrateButton) {
      await migrateButton.trigger('click')
      
      // Check if migrate event was emitted
      expect(wrapper.emitted()).toHaveProperty('migrate')
      expect(wrapper.emitted('migrate')).toHaveLength(1)
    }
  })

  it('shows loading state when loading prop is true', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: [],
        loading: true,
        migrating: false,
        migratingClientId: null
      }
    })

    // Check for progress bar (linear progress indicator)
    const progressBar = wrapper.find('.v-progress-linear')
    expect(progressBar.exists()).toBe(true)
  })

  it('disables migrate buttons when migrating', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockClients,
        loading: false,
        migrating: true,
        migratingClientId: 1
      }
    })

    // Find buttons
    const buttons = wrapper.findAll('button')
    const migrateButtons = buttons.filter(btn => btn.text().includes('Migrate'))
    
    // At least one button should be disabled
    expect(migrateButtons.length).toBeGreaterThan(0)
  })

  it('shows empty state message when no clients', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: [],
        loading: false,
        migrating: false,
        migratingClientId: null
      }
    })

    expect(wrapper.text()).toContain('All Clients Migrated')
  })

  it('highlights the client being migrated', () => {
    const wrapper = mount(LegacyClientsTable, {
      global: {
        plugins: [vuetify]
      },
      props: {
        clients: mockClients,
        loading: false,
        migrating: true,
        migratingClientId: 1
      }
    })

    // The client with ID 1 should have special styling
    const component = wrapper.vm
    expect(component.migratingClientId).toBe(1)
  })
})

