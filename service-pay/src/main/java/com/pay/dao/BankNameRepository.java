package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.BankName;
import org.springframework.stereotype.Repository;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Repository
public interface BankNameRepository extends BaseRepository<BankName, Integer> {
}
