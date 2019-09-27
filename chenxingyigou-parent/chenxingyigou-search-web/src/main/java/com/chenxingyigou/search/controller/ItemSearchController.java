package com.chenxingyigou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenxingyigou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 搜索信息
     * @param searchMap
     * @return
     */
    @RequestMapping("/search")
    public Map search(@RequestBody Map searchMap){
       return itemSearchService.search(searchMap);
    }
}
