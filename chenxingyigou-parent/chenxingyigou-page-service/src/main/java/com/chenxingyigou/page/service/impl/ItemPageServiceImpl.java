package com.chenxingyigou.page.service.impl;

import com.chenxingyigou.mapper.TbGoodsDescMapper;
import com.chenxingyigou.mapper.TbGoodsMapper;
import com.chenxingyigou.mapper.TbItemCatMapper;
import com.chenxingyigou.mapper.TbItemMapper;
import com.chenxingyigou.page.service.ItemPageService;
import com.chenxingyigou.pojo.TbGoods;
import com.chenxingyigou.pojo.TbGoodsDesc;
import com.chenxingyigou.pojo.TbItem;
import com.chenxingyigou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) throws Exception {

        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Template template = configuration.getTemplate("item.ftl");
        Map dataModel=new HashMap();
        //获取主表数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        dataModel.put("goods",tbGoods);
        //获取扩展表数据
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        dataModel.put("goodsDesc",tbGoodsDesc);
        //获取商品分类
        String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
        String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
        String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
        dataModel.put("itemCat1",itemCat1);
        dataModel.put("itemCat2",itemCat2);
        dataModel.put("itemCat3",itemCat3);

        //4.SKU列表
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//状态为有效
        criteria.andGoodsIdEqualTo(goodsId);//指定SPU ID
        example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默认
        List<TbItem> itemList = itemMapper.selectByExample(example);
        dataModel.put("itemList", itemList);

        Writer out=new FileWriter(pagedir+goodsId+".html");
        template.process(dataModel,out);//输出
        //关闭writer
        out.close();
        return false;
    }

    @Override
    public Boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for(Long goodsId:goodsIds){
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
