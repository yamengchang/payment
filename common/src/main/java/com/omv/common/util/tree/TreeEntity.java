package com.omv.common.util.tree;

import java.util.List;

/**
 * Created by zwj on 2018/9/19.
 */
public interface TreeEntity<E> {
    String getId();

    String getParentId();

    void setChildList(List<E> childList);

}
