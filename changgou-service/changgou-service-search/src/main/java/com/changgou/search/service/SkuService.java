package com.changgou.search.service;

import java.util.Map;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/12 16:42
 */
public interface SkuService
{
    /**
     * 导入数据到索引库中
     */
    void importSku();

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    Map search(Map<String, String> searchMap);
}
