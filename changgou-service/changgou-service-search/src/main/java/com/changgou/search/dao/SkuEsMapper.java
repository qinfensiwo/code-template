package com.changgou.search.dao;

import com.changgou.goods.pojo.Sku;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/12 16:51
 */
@Repository
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo, Long>
{
}
