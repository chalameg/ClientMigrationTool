package com.inpart.migration.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a Client entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Client {
    
    @EqualsAndHashCode.Include
    private Long id;
    
    private String name;
    private boolean migrated;
}

