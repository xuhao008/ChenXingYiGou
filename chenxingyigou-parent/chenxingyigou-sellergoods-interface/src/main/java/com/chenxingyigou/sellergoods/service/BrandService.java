package com.chenxingyigou.sellergoods.service;

import com.chenxingyigou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {
    /**
     * 查询所有商品品牌类型
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 品牌分页
     * @param pageNum 当前页面
     * @param pageRows 每页记录数
     * @return
     */
    public PageResult findPage(int pageNum,int pageRows);

    /**
     * 新增品牌
     * @param tbBrand
     */
    public Boolean add(TbBrand tbBrand);

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改品牌信息
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 删除品牌
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 模糊查询，分页
     * @param brand
     * @param pageNum
     * @param pageRows
     * @return
     */
    public PageResult findPage(TbBrand brand,int pageNum,int pageRows);

    /**
     * 品牌下拉框数据
     */
    List<Map> selectOptionList();
}
