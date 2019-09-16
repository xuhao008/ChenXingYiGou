package com.chenxingyigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.chenxingyigou.mapper.TbBrandMapper;
import com.chenxingyigou.pojo.TbBrand;
import com.chenxingyigou.pojo.TbBrandExample;
import com.chenxingyigou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 品牌处理实现类
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageRows) {
        PageHelper.startPage(pageNum,pageRows);//分页
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
    }

    @Override
    public Boolean add(TbBrand tbBrand) {
            System.out.println("判断品牌是否存在");
            TbBrandExample example=new TbBrandExample();
            TbBrandExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(tbBrand.getName());
            List<TbBrand> tbBrands = brandMapper.selectByExample(example);
            if (tbBrands.size()>0){
                System.out.println("该品牌已存在");
                return false;
            }else {
                System.out.println("品牌不存在开始新增");
                int insert = brandMapper.insert(tbBrand);
                if (insert>0){
                    return true;
                }
               return false;
            }
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(Long[] ids) {
       for(Long id:ids){
           brandMapper.deleteByPrimaryKey(id);
       }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageRows) {
        PageHelper.startPage(pageNum,pageRows);//分页
        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (brand!=null){
            if (brand.getName()!=null&&brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null&&brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
       return  brandMapper.selectOptionList();
    }
}
