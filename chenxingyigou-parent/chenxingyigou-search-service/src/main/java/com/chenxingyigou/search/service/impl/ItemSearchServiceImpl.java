package com.chenxingyigou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenxingyigou.pojo.TbItem;
import com.chenxingyigou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 10000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map map=new HashMap();
      //1.查询列表，高亮显示
       map.putAll(searchList(searchMap));
       //2.分组查询，商品分类列表
        List<String> list = searchCategoryList(searchMap);
        map.put("categoryList",list);
        //3.查询品牌和规格
        String category = (String) searchMap.get("category");
        if (!category.equals("")){
            map.putAll( searchBrandAndSpecList(category));
        }else{
            if (list.size()>0){
                map.putAll( searchBrandAndSpecList(list.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_goodsid").in(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //查询列表
    private Map searchList(Map searchMap){
        HighlightQuery query=new SimpleHighlightQuery();
        Map map=new HashMap();
        //空格处理
        String keywords= (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));//关键字去掉空格
        //构建高亮选项对象
        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");//后缀

        query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项

        //1.1关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按商品分类过滤
        if (!"".equals(searchMap.get("category"))){
            FilterQuery filterQuery=new SimpleFilterQuery();
            Criteria fileCriteria=new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(fileCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按商品品牌过滤
        if (!"".equals(searchMap.get("brand"))){
            FilterQuery filterQuery=new SimpleFilterQuery();
            Criteria fileCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(fileCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4按商品品牌过滤
        if (!"".equals(searchMap.get("spec"))){
           Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
           for (String key:specMap.keySet()){
               FilterQuery filterQuery=new SimpleFilterQuery();
               Criteria fileCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
               filterQuery.addCriteria(fileCriteria);
               query.addFilterQuery(filterQuery);
           }
        }
        //1.5按价格过滤
        if (!"".equals(searchMap.get("price"))){
            String[] price=((String)searchMap.get("price")).split("-");
            if (!price[0].equals("0")){//如果最低价格不等于0
                System.out.println(0);
                FilterQuery filterQuery=new SimpleFilterQuery();
                Criteria fileCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(fileCriteria);
                query.addFilterQuery(filterQuery);
            }
           if (!price[1].equals("*")) {//如果高低价格不等于*
               System.out.println(1);
               FilterQuery filterQuery=new SimpleFilterQuery();
               Criteria fileCriteria=new Criteria("item_price").lessThanEqual(price[1]);
               filterQuery.addCriteria(fileCriteria);
               query.addFilterQuery(filterQuery);
           }
        }
        //1.6分页
        Integer pageNo= (Integer) searchMap.get("pageNo");//获取页码
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize= (Integer) searchMap.get("pageSize");//获取页大小
        if (pageSize==null){
            pageSize=1;
        }
        query.setOffset((pageNo-1)*pageSize);//获取起始索引
        query.setRows(pageSize);//每页记录数


        //1.7按价格排序
        String sortValue= (String) searchMap.get("sort");//升序ASC,降序DESC
        String sortField= (String) searchMap.get("sortField");//排序字段
        if (sortValue!=null && !sortValue.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        //************获取高亮结果集***********

        //返回高亮页对象
        HighlightPage<TbItem> queryForHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每条记录的高亮入口)
        List<HighlightEntry<TbItem>> entryList = queryForHighlightPage.getHighlighted();

        for (HighlightEntry<TbItem> entry:entryList){
            //获取高亮列表（高亮域的个数）
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
           /* for (HighlightEntry.Highlight h:highlights){
                //每个域有可能存储多个值
                List<String> snipplets = h.getSnipplets();
               // System.out.println(snipplets);
            }*/
            if (highlights.size()>0&& highlights.get(0).getSnipplets().size()>0){
                TbItem entity = entry.getEntity();
                entity.setTitle( highlights.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",queryForHighlightPage.getContent());
        map.put("totalPages", queryForHighlightPage.getTotalPages());//总页数
        map.put("total",queryForHighlightPage.getTotalElements());//获取总记录数
        return  map;
    }

    /**
     * 分组查询(查询商品分类列表)
     * @return
     */
    private List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query=new SimpleQuery("*:*");
        //关键字查询 where
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));//where ....
        query.addCriteria(criteria);
        //设置分组
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");//group by ....
        query.setGroupOptions(groupOptions);
        //获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果添加到list集合
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据商品分类名称查询品牌列表和规格
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        //1.根据商品分类名称得到模板id
        Long templateId= (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //判断模板id是否存在
        if (templateId!=null) {
            //2.根据模板id获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //3.得到模板id获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);
        }
        return map;
    }
}
