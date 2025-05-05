package edu.ntnu.idatt2106.krisefikser.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Implementation of RoleHierarchy that uses a Map to define the role hierarchy. Each role in the
 * map inherits all permissions of the roles it maps to.
 */
public class MapBasedRoleHierarchy implements RoleHierarchy {

  private final Map<String, Set<String>> roleHierarchyMap;

  /**
   * Constructor that takes a Map defining the role hierarchy.
   *
   * @param roleHierarchyMap A map where keys are higher roles and values are sets of roles they
   *                         inherit from
   */
  public MapBasedRoleHierarchy(Map<String, Set<String>> roleHierarchyMap) {
    this.roleHierarchyMap = roleHierarchyMap;
  }

  /**
   * Get all reachable authorities given a set of authorities. For each authority, we find all
   * authorities it inherits and add them to the result.
   *
   * @param authorities The authorities to find all reachable authorities for
   * @return A collection of all reachable authorities
   */
  @Override
  public Collection<GrantedAuthority> getReachableGrantedAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    if (authorities == null || authorities.isEmpty()) {
      return new HashSet<>();
    }

    Set<GrantedAuthority> reachableAuthorities = new HashSet<>();

    for (GrantedAuthority authority : authorities) {
      reachableAuthorities.add(authority);
      getReachableRoles(authority.getAuthority(), reachableAuthorities);
    }

    return reachableAuthorities;
  }

  /**
   * Recursively collect all roles that are reachable from a given role.
   *
   * @param role   The role to find all reachable roles from
   * @param result The set to collect all reachable authorities in
   */
  private void getReachableRoles(String role, Set<GrantedAuthority> result) {
    Set<String> inheritedRoles = roleHierarchyMap.get(role);
    if (inheritedRoles != null) {
      for (String inheritedRole : inheritedRoles) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(inheritedRole);
        if (result.add(authority)) {
          getReachableRoles(inheritedRole, result);
        }
      }
    }
  }
}