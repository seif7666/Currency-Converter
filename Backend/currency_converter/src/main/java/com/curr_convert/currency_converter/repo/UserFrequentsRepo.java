package com.curr_convert.currency_converter.repo;

import com.curr_convert.currency_converter.model.UserFrequents;
import com.curr_convert.currency_converter.model.UserPrinciple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserFrequentsRepo extends JpaRepository<UserFrequents,Long> {

    @Query("SELECT COUNT(*) FROM UserFrequents u WHERE u.principle=?1")
    int findRecordsCountByUserId(UserPrinciple userPrinciple);
    Optional<UserFrequents> findByPrincipleAndFromCurrAndToCurr(UserPrinciple principle,String from, String to);
}
