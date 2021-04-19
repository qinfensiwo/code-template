package com.changgou.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/12 16:11
 */
@Data
@Document(indexName = "skuinfo",type = "docs")
public class SkuInfo implements Serializable
{
    //商品id，同时也是商品编号
    @Id
    private Long id;

    /**
     * SKU名称
     * FieldType.Text 类型 TEXT支持分词
     * analyzer = "ik_smart" 创建索引的分词器
     * index = true 添加数据的时候是否分词
     * store = false  是否存储
     * searchAnalyzer = "ik_smart" 搜索时使用分词器
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
    private String name;

    //商品价格，单位为：元
    @Field(type = FieldType.Double)
    private Long price;

    //库存数量
    private Integer num;

    //商品图片
    private String image;

    //商品状态，1-正常，2-下架，3-删除
    private String status;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //是否默认
    private String isDefault;

    //SPUID
    private Long spuId;

    //类目ID
    private Long categoryId;

    /**
     * 类目名称
     * type = FieldType.Keyword 不分词
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    //品牌名称
    @Field(type = FieldType.Keyword)
    private String brandName;

    //规格
    private String spec;

    //规格参数
    private Map<String,Object> specMap;

    //...略
}