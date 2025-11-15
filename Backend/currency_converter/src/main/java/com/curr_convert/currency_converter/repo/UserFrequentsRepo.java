package com.curr_convert.currency_converter.repo;

import com.curr_convert.currency_converter.model.UserFrequents;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFrequentsRepo extends JpaRepository<UserFrequents,Long> {
}
