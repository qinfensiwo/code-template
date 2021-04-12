package com.changgou.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.DeleteListenPoint;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;

/**
 * 实现MySQL数据监听
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/10 1:22
 */
@CanalEventListener
public class CanalDataEventListener
{
    /**
     *  @InsertListenPoint 增加监听
     *  rowData.getAfterColumnsList():之后的数据 增加、修改
     *  rowData.getBeforeColumnsList():之前的数据  增加、删除
     * @param eventType 当前操作的类型 增加数据
     * @param rowData 发生变更的那一行数据
     */
   /* @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for (CanalEntry.Column column : rowData.getAfterColumnsList())
        {
            System.out.println("列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }
    }*/

    /**
     *  @UpdateListenPoint 修改监听
     *  rowData.getAfterColumnsList():之后的数据 增加、修改
     *  rowData.getBeforeColumnsList():之前的数据  增加、删除
     * @param eventType 当前操作类型 增加数据
     * @param rowData 发生变更的那一行数据
     */
    /*@UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for (CanalEntry.Column column : rowData.getBeforeColumnsList())
        {
            System.out.println("修改前列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }

        for (CanalEntry.Column column : rowData.getAfterColumnsList())
        {
            System.out.println("修改后列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }
    }*/

    /**
     *  @DeleteListenPoint  删除监听
     *  rowData.getAfterColumnsList():之后的数据 增加、修改
     *  rowData.getBeforeColumnsList():之前的数据  增加、删除
     * @param eventType 当前操作的类型 增加数据
     * @param rowData 发生变更的那一行数据
     */
   /* @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        for (CanalEntry.Column column : rowData.getBeforeColumnsList())
        {
            System.out.println("删除前列明"+column.getName()+"----------变更的数据:"+column.getValue());
        }
    }*/


    /***
     * 自定义数据修改监听
     * @param eventType 监听的类型
     * @param rowData
     * schema 指定数据库
     * table 指定监控的表
     * destination 指定实例的地址
     */
  /*  @ListenPoint(destination = "example", schema = {"changgou_content"}, table = {"tb_content_category", "tb_content"}, eventType = {CanalEntry.EventType.UPDATE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.err.println("DeleteListenPoint");
        rowData.getAfterColumnsList().forEach((c) -> System.out.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
    }*/
}
