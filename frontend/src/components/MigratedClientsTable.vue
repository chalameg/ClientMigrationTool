<template>
  <v-card elevation="2">
    <v-card-title class="bg-success text-white d-flex align-center">
      <v-icon class="mr-2">mdi-check-circle</v-icon>
      Migrated Clients
      <v-spacer></v-spacer>
      <v-chip v-if="!loading" color="white" variant="outlined" size="small">
        {{ clients.length }} client{{ clients.length !== 1 ? 's' : '' }}
      </v-chip>
    </v-card-title>
    
    <v-progress-linear
      v-if="loading"
      indeterminate
      color="success"
      height="3"
    ></v-progress-linear>
    
    <v-card-text>
      <v-data-table
        :headers="headers"
        :items="clients"
        :loading="loading"
        loading-text="Loading migrated clients..."
        class="elevation-1"
        items-per-page="10"
      >
        <template v-slot:item.id="{ item }">
          <div class="d-flex align-center">
            <v-progress-circular
              v-if="rollingBackClientId === item.id"
              indeterminate
              size="16"
              width="2"
              color="warning"
              class="mr-2"
            ></v-progress-circular>
            <span :class="{ 'text-warning font-weight-bold': rollingBackClientId === item.id }">
              {{ item.id }}
            </span>
          </div>
        </template>
        
        <template v-slot:item.name="{ item }">
          <span :class="{ 'text-warning font-weight-bold': rollingBackClientId === item.id }">
            {{ item.name }}
          </span>
        </template>
        
        <template v-slot:item.migrated="{ item }">
          <v-chip color="success" size="small">
            <v-icon class="mr-1">mdi-check</v-icon>
            Migrated
          </v-chip>
        </template>
        
        <template v-slot:item.actions="{ item }">
          <v-btn
            color="warning"
            size="small"
            variant="outlined"
            @click="$emit('rollback', item.id)"
            :loading="rollingBackClientId === item.id"
            :disabled="rollingBack && rollingBackClientId !== item.id"
          >
            <v-icon class="mr-1">mdi-undo</v-icon>
            Undo
          </v-btn>
        </template>
        
        <template v-slot:no-data>
          <div class="text-center py-8">
            <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-information-outline</v-icon>
            <div class="text-h6 mb-2">No Migrations Yet</div>
            <div class="text-body-2 text-medium-emphasis">
              Start migrating clients from the legacy table above to see them here.
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
  name: 'MigratedClientsTable',
  props: {
    clients: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    rollingBack: {
      type: Boolean,
      default: false
    },
    rollingBackClientId: {
      type: Number,
      default: null
    }
  },
  emits: ['rollback'],
  data() {
    return {
      headers: [
        { title: 'ID', key: 'id', sortable: true },
        { title: 'Name', key: 'name', sortable: true },
        { title: 'Status', key: 'migrated', sortable: false, align: 'center' },
        { title: 'Actions', key: 'actions', sortable: false, align: 'center' }
      ]
    }
  }
}
</script>

