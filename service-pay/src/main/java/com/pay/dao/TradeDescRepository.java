package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.TradeDesc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Repository
public interface TradeDescRepository extends BaseRepository<TradeDesc, Integer> {

}
