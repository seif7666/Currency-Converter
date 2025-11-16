package com.curr_convert.currency_converter.repo;

import com.curr_convert.currency_converter.model.UserFrequents;
import com.curr_convert.currency_converter.model.UserPrinciple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserFrequentsRepo extends JpaRepository<UserFrequents,Long> {

    @Query("SELECT COUNT(*) FROM UserFrequents u WHERE u.principle=?1")
    int findRecordsCountByUserId(UserPrinciple userPrinciple);
    Optional<UserFrequents> findByPrincipleAndFromCurrAndToCurr(UserPrinciple principle,String from, String to);

    @Query("SELECT MIN(u2.lastUsed)  FROM UserFrequents u2 WHERE u2.principle=?1 ")
    LocalDateTime findOldestTimeId(UserPrinciple userPrinciple);

    @Modifying
    @Query("DELETE FROM UserFrequents u2 WHERE u2.principle=?1 and u2.lastUsed=?2")
    void deleteByPrincipleAndLastUsed(UserPrinciple principle,LocalDateTime dateTime);

}
