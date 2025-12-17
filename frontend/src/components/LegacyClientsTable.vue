<template>
  <v-card class="mb-6" elevation="2">
    <v-card-title class="bg-primary text-white d-flex align-center">
      <v-icon class="mr-2">mdi-database</v-icon>
      Legacy Clients
      <v-spacer></v-spacer>
      <v-chip v-if="!loading" color="white" variant="outlined" size="small">
        {{ clients.length }} client{{ clients.length !== 1 ? 's' : '' }}
      </v-chip>
    </v-card-title>
    
    <v-progress-linear
      v-if="loading"
      indeterminate
      color="primary"
      height="3"
    ></v-progress-linear>
    
    <v-card-text>
      <!-- Batch Actions Bar -->
      <v-expand-transition>
        <v-alert
          v-if="internalSelection.length > 0"
          type="info"
          variant="tonal"
          class="mb-4"
        >
          <div class="d-flex align-center justify-space-between flex-wrap ga-3">
            <div class="d-flex align-center">
              <v-icon class="mr-2">mdi-checkbox-marked-circle-outline</v-icon>
              <span class="text-body-1 font-weight-medium">
                {{ internalSelection.length }} client{{ internalSelection.length > 1 ? 's' : '' }} selected
              </span>
            </div>
            <div class="d-flex align-center ga-2">
              <v-btn
                color="primary"
                variant="elevated"
                size="default"
                @click="$emit('batch-migrate')"
                :loading="batchMigrating"
                :disabled="migrating"
              >
                <v-icon start>mdi-transfer</v-icon>
                Migrate Selected
              </v-btn>
              <v-btn
                color="grey-darken-1"
                variant="text"
                size="default"
                @click="internalSelection = []; $emit('selection-change', [])"
              >
                <v-icon start>mdi-close</v-icon>
                Clear
              </v-btn>
            </div>
          </div>
        </v-alert>
      </v-expand-transition>

      <v-data-table
        :headers="headers"
        :items="clients"
        :loading="loading"
        loading-text="Loading legacy clients..."
        class="elevation-1"
        items-per-page="10"
      >
        <template v-slot:header.id="{ }">
          <div class="d-flex align-center">
            <v-checkbox
              :model-value="allSelected"
              :indeterminate="someSelected"
              @click="toggleSelectAll"
              hide-details
              density="compact"
              class="mr-2"
            ></v-checkbox>
            ID
          </div>
        </template>
        <template v-slot:item.id="{ item }">
          <div class="d-flex align-center">
            <v-checkbox
              :model-value="isSelected(item.id)"
              @click.stop="toggleSelect(item.id)"
              hide-details
              density="compact"
              class="mr-2"
            ></v-checkbox>
            <v-progress-circular
              v-if="migratingClientId === item.id"
              indeterminate
              size="16"
              width="2"
              color="primary"
              class="mr-2"
            ></v-progress-circular>
            <span :class="{ 'text-primary font-weight-bold': migratingClientId === item.id }">
              {{ item.id }}
            </span>
          </div>
        </template>
        
        <template v-slot:item.name="{ item }">
          <span :class="{ 'text-primary font-weight-bold': migratingClientId === item.id }">
            {{ item.name }}
          </span>
        </template>
        
        <template v-slot:item.actions="{ item }">
          <v-btn
            color="primary"
            size="small"
            @click="$emit('migrate', item.id)"
            :loading="migratingClientId === item.id"
            :disabled="migrating && migratingClientId !== item.id"
          >
            <v-icon class="mr-1">mdi-transfer</v-icon>
            Migrate
          </v-btn>
        </template>
        
        <template v-slot:no-data>
          <div class="text-center py-8">
            <v-icon size="64" color="success" class="mb-4">mdi-check-circle-outline</v-icon>
            <div class="text-h6 mb-2">All Clients Migrated!</div>
            <div class="text-body-2 text-medium-emphasis">
              No legacy clients available. All clients have been successfully migrated.
            </div>
          </div>
        </template>
        
        <template v-slot:loading>
          <v-skeleton-loader type="table-row@5"></v-skeleton-loader>
        </template>
      </v-data-table>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: 'LegacyClientsTable',
  props: {
    clients: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    migrating: {
      type: Boolean,
      default: false
    },
    migratingClientId: {
      type: Number,
      default: null
    },
    batchMigrating: {
      type: Boolean,
      default: false
    },
    selectedClients: {
      type: Array,
      default: () => []
    }
  },
  emits: ['migrate', 'batch-migrate', 'selection-change'],
  data() {
    return {
      headers: [
        { title: 'ID', key: 'id', sortable: true },
        { title: 'Name', key: 'name', sortable: true },
        { title: 'Actions', key: 'actions', sortable: false, align: 'center' }
      ],
      internalSelection: []
    }
  },
  computed: {
    allSelected() {
      return this.clients.length > 0 && 
             this.internalSelection.length === this.clients.length
    },
    someSelected() {
      return this.internalSelection.length > 0 && 
             this.internalSelection.length < this.clients.length
    }
  },
  watch: {
    selectedClients: {
      immediate: true,
      handler(newVal) {
        this.internalSelection = [...newVal]
      }
    }
  },
  methods: {
    toggleSelectAll() {
      if (this.allSelected) {
        this.internalSelection = []
      } else {
        this.internalSelection = this.clients.map(c => c.id)
      }
      this.$emit('selection-change', this.internalSelection)
    },
    toggleSelect(clientId) {
      const index = this.internalSelection.indexOf(clientId)
      if (index > -1) {
        this.internalSelection.splice(index, 1)
      } else {
        this.internalSelection.push(clientId)
      }
      this.$emit('selection-change', this.internalSelection)
    },
    isSelected(clientId) {
      return this.internalSelection.includes(clientId)
    }
  }
}
</script>

