package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.Area;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Repository
public interface AreaRepository extends BaseRepository<Area, Integer> {

    List<Area> findAllByAreaId(int areaId);

    List<Area> findAllByParentId(int parentId);

}
