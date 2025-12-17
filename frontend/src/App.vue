<template>
  <v-app>
    <v-app-bar color="primary" prominent>
      <v-app-bar-title>
        <v-icon class="mr-2">mdi-swap-horizontal</v-icon>
        Client Migration Tool
      </v-app-bar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click="refreshData" :loading="loading">
        <v-icon>mdi-refresh</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <v-row>
          <v-col cols="12">
            <v-alert
              v-if="error"
              type="error"
              closable
              @click:close="error = null"
              class="mb-4"
            >
              <div class="d-flex align-center justify-space-between">
                <span>{{ error }}</span>
                <v-btn
                  size="small"
                  variant="text"
                  @click="fetchClients"
                  class="ml-4"
                >
                  Retry
                </v-btn>
              </div>
            </v-alert>
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12">
            <LegacyClientsTable
              :clients="legacyClients"
              :loading="loading"
              :migrating="migrating"
              :migrating-client-id="migratingClientId"
              :batch-migrating="batchMigrating"
              :selected-clients="selectedClients"
              @migrate="handleMigrate"
              @batch-migrate="handleBatchMigrate"
              @selection-change="handleSelectionChange"
            />
          </v-col>
        </v-row>

        <v-row>
          <v-col cols="12">
            <MigratedClientsTable
              :clients="migratedClients"
              :loading="loading"
              :rolling-back="rollingBack"
              :rolling-back-client-id="rollingBackClientId"
              @rollback="handleRollback"
            />
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <!-- Success Snackbar -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar.show = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script>
import LegacyClientsTable from './components/LegacyClientsTable.vue'
import MigratedClientsTable from './components/MigratedClientsTable.vue'
import api from './services/api'

export default {
  name: 'App',
  components: {
    LegacyClientsTable,
    MigratedClientsTable
  },
  data() {
    return {
      legacyClients: [],
      migratedClients: [],
      loading: false,
      migrating: false,
      migratingClientId: null,
      rollingBack: false,
      rollingBackClientId: null,
      selectedClients: [],
      batchMigrating: false,
      error: null,
      snackbar: {
        show: false,
        message: '',
        color: 'success'
      }
    }
  },
  mounted() {
    this.fetchClients()
  },
  methods: {
    async fetchClients() {
      this.loading = true
      this.error = null
      
      try {
        const [legacyResponse, migratedResponse] = await Promise.all([
          api.getLegacyClients(),
          api.getMigratedClients()
        ])
        
        this.legacyClients = legacyResponse.data
        this.migratedClients = migratedResponse.data
      } catch (err) {
        this.error = 'Failed to load clients. Please ensure the backend is running on port 9091.'
        console.error('Error fetching clients:', err)
      } finally {
        this.loading = false
      }
    },
    
    async handleMigrate(clientId) {
      this.migrating = true
      this.migratingClientId = clientId
      this.error = null
      
      // Find the client name for better UX
      const client = this.legacyClients.find(c => c.id === clientId)
      const clientName = client ? client.name : `Client ${clientId}`
      
      try {
        await api.migrateClient(clientId)
        
        // Show success message with client name
        this.showSnackbar(`${clientName} migrated successfully!`, 'success')
        
        // Remove from selection if it was selected
        this.selectedClients = this.selectedClients.filter(id => id !== clientId)
        
        // Small delay for animation effect
        await new Promise(resolve => setTimeout(resolve, 300))
        
        // Refresh the client lists
        await this.fetchClients()
      } catch (err) {
        if (err.response && err.response.data && err.response.data.error) {
          this.error = err.response.data.error
        } else {
          this.error = `Failed to migrate ${clientName}. Please try again.`
        }
        console.error('Error migrating client:', err)
      } finally {
        this.migrating = false
        this.migratingClientId = null
      }
    },
    
    async handleBatchMigrate() {
      if (this.selectedClients.length === 0) return
      
      this.batchMigrating = true
      this.error = null
      
      const clientsToMigrate = [...this.selectedClients]
      const clientNames = clientsToMigrate.map(id => {
        const client = this.legacyClients.find(c => c.id === id)
        return client ? client.name : `Client ${id}`
      })
      
      try {
        // Migrate clients sequentially for better UX (can see each one)
        for (const clientId of clientsToMigrate) {
          this.migratingClientId = clientId
          await api.migrateClient(clientId)
          
          // Remove from selection
          this.selectedClients = this.selectedClients.filter(id => id !== clientId)
          
          // Small delay between migrations for animation
          await new Promise(resolve => setTimeout(resolve, 400))
          
          // Refresh to show progress
          await this.fetchClients()
        }
        
        // Show success message
        const count = clientsToMigrate.length
        this.showSnackbar(
          `Successfully migrated ${count} client${count > 1 ? 's' : ''}!`,
          'success'
        )
      } catch (err) {
        if (err.response && err.response.data && err.response.data.error) {
          this.error = err.response.data.error
        } else {
          this.error = 'Failed to migrate some clients. Please try again.'
        }
        console.error('Error batch migrating clients:', err)
      } finally {
        this.batchMigrating = false
        this.migratingClientId = null
      }
    },
    
    handleSelectionChange(selected) {
      this.selectedClients = selected
    },
    
    async handleRollback(clientId) {
      this.rollingBack = true
      this.rollingBackClientId = clientId
      this.error = null
      
      // Find the client name for better UX
      const client = this.migratedClients.find(c => c.id === clientId)
      const clientName = client ? client.name : `Client ${clientId}`
      
      try {
        await api.rollbackClient(clientId)
        
        // Show success message with client name
        this.showSnackbar(`${clientName} rolled back to legacy status`, 'warning')
        
        // Refresh the client lists
        await this.fetchClients()
      } catch (err) {
        if (err.response && err.response.data && err.response.data.error) {
          this.error = err.response.data.error
        } else {
          this.error = `Failed to rollback ${clientName}. Please try again.`
        }
        console.error('Error rolling back client:', err)
      } finally {
        this.rollingBack = false
        this.rollingBackClientId = null
      }
    },
    
    refreshData() {
      this.fetchClients()
      this.showSnackbar('Data refreshed', 'info')
    },
    
    showSnackbar(message, color = 'success') {
      this.snackbar.message = message
      this.snackbar.color = color
      this.snackbar.show = true
    }
  }
}
</script>

<style>
.v-app {
  font-family: 'Roboto', sans-serif;
  background-color: #f9fafc;
}

.v-main {
  background-color: #f9fafc;
}

/* Smooth animations for row changes */
.v-data-table tbody tr {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-data-table tbody tr:hover {
  background-color: rgba(71, 73, 255, 0.04);
}

/* Fade animation for migrating rows */
@keyframes fadeOut {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(20px);
  }
}

/* Smooth transitions for cards */
.v-card {
  transition: all 0.3s ease;
}

/* Selection highlight */
.v-data-table tbody tr.selected {
  background-color: rgba(71, 73, 255, 0.08);
}
</style>

