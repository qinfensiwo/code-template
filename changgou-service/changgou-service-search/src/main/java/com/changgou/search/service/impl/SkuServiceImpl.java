package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/12 16:43
 */
@Service
public class SkuServiceImpl implements SkuService
{
    private final SkuFeign skuFeign;
    private final SkuEsMapper skuEsMapper;
    private final ElasticsearchTemplate elasticsearchTemplate;

    public SkuServiceImpl(SkuFeign skuFeign,
                          SkuEsMapper skuEsMapper,
                          ElasticsearchTemplate elasticsearchTemplate)
    {
        this.skuFeign = skuFeign;
        this.skuEsMapper = skuEsMapper;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }


    /**
     * 导入sku数据到es
     */
    @Override
    public void importSku()
    {
        //调用changgou-service-goods微服务
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        //Result<List<Sku>> skuListResult = skuFeign.findAll();
        //将数据转成search.Sku
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skuListResult.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos)
        {
            //获取规格的数据  {"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            //如果要生成动态的域，只需将该域存入到一个Map<String, Object>对象中即可，该对象的key会生成一个域，域的名字为该Map的Key。
            //转成MAP  key: 规格的名称  value:规格的选项的值
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    /**
     * 多条件搜索
     * * @Param: searchMap
     *
     * @return : java.util.Map
     * @author : Mr.Wang
     * @time : 2021/4/13 0:11
     */
    @Override
    public Map search(Map<String, String> searchMap)
    {
        /*搜索条件封装*/
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBaseQuery(searchMap);
        /*集合搜索*/
        Map<String, Object> resultMap = serachList(nativeSearchQueryBuilder);

        //当用户选择了分类，将分类作为搜索条件，则不需要对分类进行分组搜索，因为分组搜索的数据是用于显示分类条件的
//        if (searchMap == null || StringUtils.isEmpty(searchMap.get("category")))
//        {
//            /*分类分组查询实现*/
//            List<String> categoryList = searchCategoryList(nativeSearchQueryBuilder);
//            resultMap.put("categoryList", categoryList);
//        }

        //当用户选择了品牌，将品牌作为搜索条件，则不需要对品牌进行分组搜索，因为分组搜索的数据是用于显示品牌条件的
//        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand")))
//        {
//            /*分类分组查询实现*/
//            List<String> brandList = searchBrandList(nativeSearchQueryBuilder);
//            resultMap.put("brandList", brandList);
//        }
        /*品牌分组查询实现*/

        /*规格分组查询实现*/
        //Map<String, Set<String>> specMap = searchSpecMap(nativeSearchQueryBuilder);

        /*resultMap.put("specList",specList);*/
        //resultMap.put("specMap", specMap);

        Map<String, Object> groupMap = searchGroupList(nativeSearchQueryBuilder, searchMap);
        resultMap.putAll(groupMap);
        return resultMap;
    }


    /**
     * 搜索条件封装
     *
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder buildBaseQuery(Map<String, String> searchMap)
    {
        //NativeSearchQueryBuilder 搜索条件构件对象 用于封装各种搜索条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //BoolQuery     must,must_not,should
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        /*根据关键词搜索*/
        if (searchMap != null && searchMap.size() > 0)
        {
            String keywords = searchMap.get("keywords");
            //若关键词不为空，则搜索关键词数据
            if (!StringUtils.isEmpty(keywords))
            {
                /*nativeSearchQueryBuilder.withQuery(QueryBuilders.queryStringQuery(keywords).field("name"));*/
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }

            //分类过滤
            if (!StringUtils.isEmpty(searchMap.get("category")))
            {
                boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
            }

            //品牌过滤
            if (!StringUtils.isEmpty(searchMap.get("brand")))
            {
                boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
            }
            //规格过滤
            if (searchMap != null)
            {
                for (String key : searchMap.keySet())
                {
                    if (key.startsWith("spec_"))
                    {
                        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", searchMap.get(key)));
                    }
                }
            }

            //价格过滤
            //price 0-500元 500-1000元
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price))
            {
                //去掉中文后 0-500 500-1000
                price = price.replace("元", "").replace("以上", "");
                // prices[] 根据- 分割 [0,500]、[500,1000]
                String[] prices = price.split("-");
                //x不一定为空 ,y可能为null
                if (prices != null && prices.length > 0)
                {
                    //prices[0] !=null  price>prices[0]
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                    //prices[1] !=null price<prices[1]
                    if (prices.length == 2)
                    {
                        //price<prices[1]
                        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));
                    }
                }

                /*if (!StringUtils.isEmpty(price))
                {
                    String[] split = price.split("-");
                    if (!split[1].equalsIgnoreCase("*"))
                    {
                        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
                    }
                    else
                    {
                        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
                    }
                }*/
            }
            //排序实现
            //指定排序的域
            String sortField = searchMap.get("sortField");
            //指定排序的规则
            String sortRule = searchMap.get("sortRule");
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule))
            {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equalsIgnoreCase("DESC")? SortOrder.DESC:SortOrder.ASC));
            }
        }
        //分页，如果用户默认不传，则默认第1页
        Integer pageNum = coverterPage(searchMap);
        Integer size = 10;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, size));

        //将boolQueryBuilder填充给nativeSearchQueryBuilder
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }


    /**
     * 接收前端传入的分页参数
     *
     * @param searchMap
     * @return
     */
    public Integer coverterPage(Map<String, String> searchMap)
    {
        if (searchMap != null)
        {
            String pageNum = searchMap.get("pageNum");
            try
            {
                return Integer.parseInt(pageNum);
            }
            catch (NumberFormatException e)
            {
//                e.printStackTrace();
//                return 1;
            }
        }
        return 1;
    }

    /**
     * 集合搜索
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Object> serachList(NativeSearchQueryBuilder nativeSearchQueryBuilder)
    {
        /*高亮配置*/
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");
        //前缀 <em style="color:red;">  后缀</em>
        field.preTags("<em style=\"color:red;\">");
        field.postTags("</em>");
        //碎片长度 关键词数据长度
        field.fragmentSize(100);
        /*添加高亮*/
        nativeSearchQueryBuilder.withHighlightFields(field);

        /**
         * 1.搜索条件封装
         * 2.搜索的结果集（集合数据）需要转换的类型
         * 3.AggregatedPage<SkuInfo>：搜索结果集的封装
         */
        //AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(
                //搜索条件的封装
                nativeSearchQueryBuilder.build(),
                //数据集合要转换的类型的字节码
                SkuInfo.class,
                //执行搜索后，将数据结果集封装到该对象中
                //SearchResultMapper
                new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable)
                    {
                        List<T> content = new ArrayList<>();
                        if (searchResponse.getHits() == null || searchResponse.getHits().getTotalHits() <= 0) {
                            return new AggregatedPageImpl<T>(content);
                        }
                        //执行查询，获取所有数据->结果集[ 非高亮数据|高亮数据 ]
                        for (SearchHit hit : searchResponse.getHits())
                        {
                            //分析结果集数据，获取高量数据->只有某个域的高量数据
                            String sourceAsString = hit.getSourceAsString();
                            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
                            //非高量数据中指定的域替换成高亮数据
                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                            HighlightField highlightField = highlightFields.get("name");
                            //将数据返回

                            //有高亮则设置高亮的值
                            if (highlightField != null) {
                                StringBuffer stringBuffer = new StringBuffer();
                                for (Text text : highlightField.getFragments()) {
                                    stringBuffer.append(text.string());
                                }
                                skuInfo.setName(stringBuffer.toString());
                            }
                            content.add((T) skuInfo);
                        }

                        return new AggregatedPageImpl<T>(content,pageable,searchResponse.getHits().getTotalHits(),searchResponse.getAggregations(),searchResponse.getScrollId());
                    }
                });


        /* 分页参数 总记录数*/
        long totalElements = page.getTotalElements();
        /*总页数*/
        int totalPages = page.getTotalPages();

        /*获取结果集*/
        List<SkuInfo> contents = page.getContent();

        /*封装一个Map存储所有数据，并返回*/
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("rows", contents);
        resultMap.put("total", totalElements);
        resultMap.put("totalPages", totalPages);
        return resultMap;
    }

    /**
     * 品牌分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder)
    {
        NativeSearchQueryBuilder brand = nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName").size(20));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(brand.build(), SkuInfo.class);
        StringTerms stringTermsBrand = aggregatedPage.getAggregations().get("skuBrandGroup");
        List<String> brandList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTermsBrand.getBuckets())
        {
            String brandName = bucket.getKeyAsString();
            brandList.add(brandName);
        }
        return brandList;
    }

    /**
     * 分组查询  -> 分类、品牌、规格分组
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String,Object> searchGroupList(NativeSearchQueryBuilder nativeSearchQueryBuilder,Map<String, String> searchMap)
    {
        /**
         * 分组查询分类集合
         * addAggregation() 添加一个聚合操作
         * 1.取别名
         * 2.表示根据哪个域进行分组查询
         */
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("category")))
        {
             nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategoryGroup").field("categoryName").size(20));
        }
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand")))
        {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName").size(20));
        }
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(200));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        /**
         * 获取分组数据
         * aggregatedPage.getAggregations() 获取的是集合，可以根据多个域进行分组
         * .get("skuCategoryGroup") 获取指定域的集合数
         */
        Map<String, Object> groupMap = new HashMap<>();
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("category")))
        {
            StringTerms stringTermsCategory = aggregatedPage.getAggregations().get("skuCategoryGroup");
            List<String> categoryList = getGroupList(stringTermsCategory);
            groupMap.put("categoryList",categoryList);
        }
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand")))
        {
            StringTerms stringTermsBrand = aggregatedPage.getAggregations().get("skuBrandGroup");
            List<String> brandList = getGroupList(stringTermsBrand);
            groupMap.put("brandList",brandList);
        }
        StringTerms stringTermsSpec = aggregatedPage.getAggregations().get("skuSpecGroup");

        List<String> specList = getGroupList(stringTermsSpec);
        Map<String, Set<String>> specMap =  putAllSpec(specList);
        groupMap.put("specList",specMap);
        return groupMap;

    }

    private List<String> getGroupList(StringTerms stringTermsCategory)
    {
        List<String> groupList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTermsCategory.getBuckets())
        {
            String fieldName = bucket.getKeyAsString();
            groupList.add(fieldName);
        }
        return groupList;
    }

    /**
     * 分类分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder)
    {
        /**
         * 分组查询分类集合
         * addAggregation() 添加一个聚合操作
         * 1.取别名
         * 2.表示根据哪个域进行分组查询
         */
        NativeSearchQueryBuilder category = nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategoryGroup").field("categoryName").size(20));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(category.build(), SkuInfo.class);
        /**
         * 获取分组数据
         * aggregatedPage.getAggregations() 获取的是集合，可以根据多个域进行分组
         * .get("skuCategoryGroup") 获取指定域的集合数
         */
        StringTerms stringTermsCategory = aggregatedPage.getAggregations().get("skuCategoryGroup");
        List<String> categoryList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTermsCategory.getBuckets())
        {
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }
        return categoryList;
    }

    /**
     * 规格分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Set<String>> searchSpecMap(NativeSearchQueryBuilder nativeSearchQueryBuilder)
    {
        NativeSearchQueryBuilder skuSpecGroup = nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(200));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(skuSpecGroup.build(), SkuInfo.class);
        StringTerms stringTermsSpec = aggregatedPage.getAggregations().get("skuSpecGroup");
        List<String> specList = new ArrayList<>();
        if (stringTermsSpec != null)
        {
            for (StringTerms.Bucket bucket : stringTermsSpec.getBuckets())
            {
                /* "{\"手机屏幕尺寸\":\"5.5寸\",\"网络\":\"移动4G\",\"颜色\":\"红\",\"测试\":\"实施\",\"机身内存\":\"32G\",\"存储\":\"32G\",\"像素\":\"800万像素\"}"*/
                String spec = bucket.getKeyAsString();
                specList.add(spec);
            }
        }

        Map<String, Set<String>> specMap = putAllSpec(specList);
        return specMap;
    }


    /**
     * 规格汇总合并
     * @param specList
     * @return
     */
    private Map<String, Set<String>> putAllSpec(List<String> specList)
    {
        /**
         * 规格汇总
         */
        //合并后的Map对象 将每个Map对象合成成一个Map<String,Set<String>>
        Map<String, Set<String>> specMap = new HashMap<>();
        /*1.循环specList*/
        for (String spec : specList)
        {
            /*2.将每个JSON字符串转换成Map*/
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            //4.合并流程
            for (Map.Entry<String, String> entry : map.entrySet())
            {
                //* 4.1取出当前Map,并且获取对应的Key，以及对应的Value
                //规格名字
                String key = entry.getKey();
                //规格值
                String value = entry.getValue();
                //* 4.2将当前循环的数据合并到一个Map<String,Set<String>>中
                Set<String> specSet = specMap.get(key);
                if (specSet == null)
                {
                    specSet = new HashSet<>();
                }
                specSet.add(value);
                specMap.put(key, specSet);
            }
        }
        return specMap;
    }


}
