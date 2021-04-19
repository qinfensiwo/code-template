package com.changgou.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.entity.Result;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.DeleteListenPoint;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * 实现MySQL数据监听
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/10 1:22
 */
@CanalEventListener
public class CanalDataEventListener
{

    @Autowired
    private ContentFeign contentFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

  /*  private final ContentFeign contentFeign;
    private final StringRedisTemplate stringRedisTemplate;

    public CanalDataEventListener(ContentFeign contentFeign, StringRedisTemplate stringRedisTemplate) {
        this.contentFeign = contentFeign;
        this.stringRedisTemplate = stringRedisTemplate;
    }*/

    /**
     *  @InsertListenPoint 增加监听
     *  rowData.getAfterColumnsList():之后的数据 增加、修改
     *  rowData.getBeforeColumnsList():之前的数据  增加、删除
     * @param eventType 当前操作的类型 增加数据
     * @param rowData 发生变更的那一行数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for (CanalEntry.Column column : rowData.getAfterColumnsList())
        {
            System.out.println("列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }
    }

    /**
     *  @UpdateListenPoint 修改监听
     *  rowData.getAfterColumnsList():之后的数据 增加、修改
     *  rowData.getBeforeColumnsList():之前的数据  增加、删除
     * @param eventType 当前操作类型 增加数据
     * @param rowData 发生变更的那一行数据
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for (CanalEntry.Column column : rowData.getBeforeColumnsList())
        {
            System.out.println("修改前列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }

        for (CanalEntry.Column column : rowData.getAfterColumnsList())
        {
            System.out.println("修改后列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }
    }

    /**
     *  @DeleteListenPoint  删除监听
     *  rowData.getAfterColumnsList():之后的数据 增加、修改
     *  rowData.getBeforeColumnsList():之前的数据  增加、删除
     * @param eventType 当前操作的类型 增加数据
     * @param rowData 发生变更的那一行数据
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for (CanalEntry.Column column : rowData.getBeforeColumnsList())
        {
            System.out.println("删除前列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }
    }


    /***
     * 自定义数据修改监听
     * @param eventType 监听的类型
     * @param rowData
     * schema 指定数据库
     * table 指定监控的表
     * destination 指定实例的地址
     */
    @ListenPoint(destination = "example",
            schema = {"changgou_content"},
            table = {"tb_content_category", "tb_content"},
            eventType = {CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE,
                    CanalEntry.EventType.INSERT})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.获取列名 为category_id的值
        String categoryId = getColumnValue(eventType,rowData);
        //2.调用feign 获取该分类下的所有的广告集合
        Result<List<Content>> categoryResult = contentFeign.findByCategory(Long.valueOf(categoryId));
        List<Content> data = categoryResult.getData();
        //3.使用redisTemplate存储到redis中
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(data));
    }


    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData)
    {
        String categoryId = "";
        //判断 如果是删除  则获取beforlist
        if (eventType  == CanalEntry.EventType.DELETE){
            for (CanalEntry.Column column : rowData.getBeforeColumnsList())
            {
                if (column.getName().equalsIgnoreCase("category_id")){
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }else {
            //判断 如果是添加 或者是更新 获取afterlist
            for (CanalEntry.Column column : rowData.getAfterColumnsList())
            {
                if (column.getName().equalsIgnoreCase("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        return categoryId;
    }
}
