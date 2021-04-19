package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/12 17:09
 */

@RestController
@RequestMapping(value = "/search")
@CrossOrigin
public class SkuController
{
    private final SkuService skuService;
    public SkuController(SkuService skuService) {this.skuService = skuService;}


    /**
     * 导入数据
     * @return
     */
    @GetMapping("/import")
    public Result importData(){
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入数据到索引库中成功！");
    }

    /**
     * 搜索
     * * @Param: searchMap
     * @return : java.util.Map
     * @author : Mr.Wang
     * @time : 2021/4/13 0:10
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap){
        return skuService.search(searchMap);
    }
}
