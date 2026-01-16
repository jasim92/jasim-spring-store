package com.jasim.store.repositories;

import com.jasim.store.entities.Profile;
import com.jasim.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfileRepository extends CrudRepository<Profile, Long> {

     List<Profile> findProfileByLoyaltyPointsGreaterThan(Integer loyaltyPoints);
}