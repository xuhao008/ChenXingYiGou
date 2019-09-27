package com.chenxingyigou.solrutil;

import com.alibaba.fastjson.JSON;
import com.chenxingyigou.mapper.TbItemMapper;
import com.chenxingyigou.pojo.TbItem;
import com.chenxingyigou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){

        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        List<TbItem> tbItems = itemMapper.selectByExample(example);//查询SKU
        System.out.println("--商品列表--");
        for (TbItem item:tbItems){
            System.out.println(item.getId()+""+item.getTitle()+""+item.getPrice());
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);//提取数据库规格json字符串转换为map
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();//存入solr
        System.out.println("--结束--");
    }

    /**
     * 删除
     */
    public void deleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
    /**
     * 从数据库向solr存值
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
      //solrUtil.importItemData();
        solrUtil.deleteAll();

    }
}
