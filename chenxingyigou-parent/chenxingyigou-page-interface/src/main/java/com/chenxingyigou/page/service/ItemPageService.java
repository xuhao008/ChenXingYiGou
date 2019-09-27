package com.chenxingyigou.page.service;

public interface ItemPageService {

    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     */
    public boolean genItemHtml(Long goodsId) throws Exception;

    /**
     * 删除商品详细页
     * @param goodsId
     * @return
     */
    public Boolean deleteItemHtml(Long[]goodsId);
}
