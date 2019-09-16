package com.chenxingyigou.pojogroup;

import com.chenxingyigou.pojo.TbGoods;
import com.chenxingyigou.pojo.TbGoodsDesc;
import com.chenxingyigou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * 商品组合实体类
 */
public class Goods implements Serializable {
    private TbGoods goods;//商品基本信息SPU
    private TbGoodsDesc goodsDesc;//商品扩展信息

    private List<TbItem> itemList;//商品SKU列表
    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemsList) {
        this.itemList = itemsList;
    }

}
