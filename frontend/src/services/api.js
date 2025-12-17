import axios from 'axios'

// Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9091/api'
const TIMEOUT = 10000 // 10 seconds

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - can add auth tokens, logging, etc.
apiClient.interceptors.request.use(
  (config) => {
    // You can add auth headers here in the future
    // config.headers.Authorization = `Bearer ${token}`
    console.log(`[API Request] ${config.method.toUpperCase()} ${config.url}`)
    return config
  },
  (error) => {
    console.error('[API Request Error]', error)
    return Promise.reject(error)
  }
)

// Response interceptor - handle errors globally
apiClient.interceptors.response.use(
  (response) => {
    console.log(`[API Response] ${response.status} ${response.config.url}`)
    return response
  },
  (error) => {
    // Global error handling
    if (error.response) {
      // Server responded with error status
      console.error('[API Error]', error.response.status, error.response.data)
    } else if (error.request) {
      // Request made but no response
      console.error('[API Error] No response received', error.request)
    } else {
      // Something else happened
      console.error('[API Error]', error.message)
    }
    return Promise.reject(error)
  }
)

// Client Service - all client-related API calls
const clientService = {
  /**
   * Fetches all legacy (non-migrated) clients
   * @returns {Promise} Promise resolving to legacy clients array
   */
  getLegacyClients() {
    return apiClient.get('/legacy/clients')
  },

  /**
   * Fetches all migrated clients
   * @returns {Promise} Promise resolving to migrated clients array
   */
  getMigratedClients() {
    return apiClient.get('/new/clients')
  },

  /**
   * Fetches all clients (both legacy and migrated)
   * @returns {Promise} Promise resolving to all clients
   */
  async getAllClients() {
    const [legacy, migrated] = await Promise.all([
      this.getLegacyClients(),
      this.getMigratedClients()
    ])
    return {
      legacy: legacy.data,
      migrated: migrated.data
    }
  }
}

// Migration Service - all migration-related API calls
const migrationService = {
  /**
   * Migrates a client by their ID
   * @param {number} clientId - The ID of the client to migrate
   * @returns {Promise} Promise resolving to migrated client
   */
  migrateClient(clientId) {
    return apiClient.post(`/migrate/${clientId}`)
  },

  /**
   * Rolls back a client migration by their ID
   * @param {number} clientId - The ID of the client to rollback
   * @returns {Promise} Promise resolving to rolled back client
   */
  rollbackClient(clientId) {
    return apiClient.post(`/rollback/${clientId}`)
  },

  /**
   * Batch migrate multiple clients
   * @param {number[]} clientIds - Array of client IDs to migrate
   * @returns {Promise} Promise resolving to array of results
   */
  async batchMigrateClients(clientIds) {
    const results = await Promise.allSettled(
      clientIds.map(id => this.migrateClient(id))
    )
    return results
  }
}

// Export both services and the base client
export default {
  ...clientService,
  ...migrationService
}

// Also export individual services for better tree-shaking and modularity
export { clientService, migrationService, apiClient }


