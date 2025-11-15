package com.curr_convert.currency_converter.repo;

import com.curr_convert.currency_converter.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepo extends JpaRepository<UserPreferences,Long> {
}
