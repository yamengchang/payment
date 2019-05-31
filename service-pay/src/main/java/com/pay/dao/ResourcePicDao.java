package com.pay.dao;

import com.omv.database.repository.BaseRepository;
import com.pay.entity.ResourcePic;

import java.util.List;

public interface ResourcePicDao extends BaseRepository<ResourcePic,String> {
    void deleteByResourceId(String id);

    List<ResourcePic> findByResourceId(String id);
}
