package com.inpart.migration.repository;

import com.inpart.migration.domain.Client;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the OrganizationRepository.
 * Uses a ConcurrentHashMap for thread-safe operations.
 * Pre-populated with sample legacy clients.
 */
@Repository
public class InMemoryClientRepository implements OrganizationRepository {
    
    private final Map<Long, Client> clientStore = new ConcurrentHashMap<>();
    private Long currentId = 1L;

    public InMemoryClientRepository() {
        // Initialize with sample legacy clients
        initializeSampleData();
    }

    private void initializeSampleData() {
        save(new Client(currentId++, "UCB Alliance Managers", false));
        save(new Client(currentId++, "Lilly", false));
        save(new Client(currentId++, "Shadow Lake Group", false));
        save(new Client(currentId++, "Ferring", false));
        save(new Client(currentId++, "University of North Texas", false));
        save(new Client(currentId++, "Poseidon LLC", false));
    }

    @Override
    public Optional<Client> findById(Long id) {
        return Optional.ofNullable(clientStore.get(id));
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clientStore.values());
    }

    @Override
    public List<Client> findByMigratedFalse() {
        return clientStore.values().stream()
                .filter(client -> !client.isMigrated())
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> findByMigratedTrue() {
        return clientStore.values().stream()
                .filter(Client::isMigrated)
                .collect(Collectors.toList());
    }

    @Override
    public Client save(Client client) {
        if (client.getId() == null) {
            client.setId(currentId++);
        }
        clientStore.put(client.getId(), client);
        return client;
    }
}

