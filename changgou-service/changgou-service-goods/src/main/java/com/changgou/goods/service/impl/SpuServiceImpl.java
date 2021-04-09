package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.IdWorker;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:admin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService
{

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size)
    {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size)
    {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu)
    {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu)
    {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null)
        {
            // 主键
            if (!StringUtils.isEmpty(spu.getId()))
            {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn()))
            {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName()))
            {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption()))
            {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId()))
            {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id()))
            {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id()))
            {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id()))
            {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId()))
            {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId()))
            {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage()))
            {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages()))
            {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService()))
            {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction()))
            {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems()))
            {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems()))
            {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum()))
            {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum()))
            {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if (!StringUtils.isEmpty(spu.getIsMarketable()))
            {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec()))
            {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if (!StringUtils.isEmpty(spu.getIsDelete()))
            {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if (!StringUtils.isEmpty(spu.getStatus()))
            {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Long id)
    {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //检查是否被逻辑删除  ,必须先逻辑删除后才能物理删除
        if(!spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品不能删除！");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu)
    {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu)
    {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id)
    {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll()
    {
        return spuMapper.selectAll();
    }

    /***
     * 保存Goods
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods)
    {
        //增加Spu
        Spu spu = goods.getSpu();
        if (spu.getId() == null){
            //增加
            spu.setId(idWorker.nextId());
            spuMapper.insertSelective(spu);
        }else{
            //修改数据
            spuMapper.updateByPrimaryKeySelective(spu);
            //删除spu下的Sku
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }


        //增加SKU
        Date date = new Date();
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //获取Sku集合
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList)
        {
            if (StringUtils.isEmpty(sku.getSpec()))
            {
                sku.setSpec("{}");
            }
            //获取Spu名
            String name = spu.getName();
            //将规格转换成map
            Map<String,String> specMap = JSON.parseObject(sku.getSpec(),Map.class);
            //循环组装Sku的名字
            for (Map.Entry<String, String> entry : specMap.entrySet())
            {
                name += " "+entry.getValue();
            }
            sku.setId(idWorker.nextId());
            sku.setName(name);
            sku.setSpuId(spu.getId());
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setCategoryId(spu.getCategory3Id());
            sku.setCategoryName(category.getName());
            sku.setBrandName(brand.getName());
            //增加
            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public Goods findGoodsById(Long SpuId)
    {
        //查询SPU
        Spu spu = spuMapper.selectByPrimaryKey(SpuId);

        //查询List<Sku>
        Sku sku = new Sku();
        sku.setSpuId(SpuId);
        List<Sku> skuList = skuMapper.select(sku);
        //封装Goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 商品审核
     * * @Param: id
     * @return : void
     * @author : Mr.Wang
     * @time : 2021/4/8 15:20
     */
    @Override
    public void audit(Long id)
    {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //判断商品是否已经删除
        if (spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("该商品已被删除");
        }
        //实现上架
        //审核通过
        spu.setStatus("1");
        //上架
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品下架
     * * @Param: id
     * @return : void
     * @author : Mr.Wang
     * @time : 2021/4/8 15:27
     */
    @Override
    public void pull(Long id)
    {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //判断商品是否已经删除
        if (spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("该商品已被删除");
        }
        //更改为下架状态
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品上架
     * * @Param: id
     * @return : void
     * @author : Mr.Wang
     * @time : 2021/4/8 15:56
     */
    @Override
    public void put(Long id)
    {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //判断商品是否已经删除
        if (spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("该商品已被删除");
        }
        if(!spu.getStatus().equals("1")){
            throw new RuntimeException("未通过审核的商品不能！");
        }
        //上架
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 批量上架
     * * @Param: ids
     * @return : int
     * @author : Mr.Wang
     * @time : 2021/4/8 16:00
     */
    @Override
    public int putMany(Long[] ids)
    {
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        //批量修改
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        //下架
        criteria.andEqualTo("isMarketable","0");
        //审核通过
        criteria.andEqualTo("status","1");
        //非删除的
        criteria.andEqualTo("isDelete","0");
        return spuMapper.updateByExampleSelective(spu,example);
    }

    @Override
    public int pullMany(Long[] ids)
    {
        Spu spu = new Spu();
        spu.setIsMarketable("0");
        Class entityClass;
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isMarketable","1");
        //审核通过
        criteria.andEqualTo("status","1");
        //非删除的
        criteria.andEqualTo("isDelete","0");
        return spuMapper.updateByExampleSelective(spu,example);
    }

    /**
     * 根据Id逻辑删除商品
     * * @Param: id
     * @return : void
     * @author : Mr.Wang
     * @time : 2021/4/9 10:19
     */
    @Override
    public void logicDelete(Long id)
    {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (!spu.getIsMarketable().equals("0")){
            throw new RuntimeException("必须先下架商品再删除");
        }
        spu.setIsDelete("1");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 恢复商品
     * @param id
     */
    @Override
    public void restore(Long id)
    {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (!spu.getIsDelete().equals("1")){
            throw new RuntimeException("该商品未被删除");
        }
        spu.setIsDelete("0");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }
}
