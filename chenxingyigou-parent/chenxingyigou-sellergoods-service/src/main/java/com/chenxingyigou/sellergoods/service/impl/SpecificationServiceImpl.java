package com.chenxingyigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.chenxingyigou.mapper.TbSpecificationMapper;
import com.chenxingyigou.mapper.TbSpecificationOptionMapper;
import com.chenxingyigou.pojo.TbSpecification;
import com.chenxingyigou.pojo.TbSpecificationExample;
import com.chenxingyigou.pojo.TbSpecificationExample.Criteria;
import com.chenxingyigou.pojo.TbSpecificationOption;
import com.chenxingyigou.pojo.TbSpecificationOptionExample;
import com.chenxingyigou.pojogroup.Specification;
import com.chenxingyigou.sellergoods.service.SpecificationService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//获取实体类
		TbSpecification tbSpecification=specification.getSpecification();

		specificationMapper.insert(tbSpecification);
		//获取规格选项集合
		List<TbSpecificationOption> specifications=specification.getSpecificationOptionList();
		for (TbSpecificationOption option:specifications){
			option.setSpecId(tbSpecification.getId());//设置规格id
			specificationOptionMapper.insert(option);//新增规格
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
        //获取实体类
        TbSpecification tbSpecification=specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        //删除原来规格对应的数据
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());

        specificationOptionMapper.deleteByExample(example);


        //获取规格选项集合
        List<TbSpecificationOption> specifications=specification.getSpecificationOptionList();
        for (TbSpecificationOption option:specifications){
            option.setSpecId(tbSpecification.getId());//设置规格id
            specificationOptionMapper.insert(option);//新增规格
        }
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification=new Specification();
		//获取规格实体
		TbSpecification tbSpecification=specificationMapper.selectByPrimaryKey(id);
		specification.setSpecification(tbSpecification);

		//获取规格选项列表
        TbSpecificationOptionExample example=new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
        specification.setSpecificationOptionList(tbSpecificationOptions);
        return specification; /*specificationMapper.selectByPrimaryKey(id);*/
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    //删除规格表数据
			specificationMapper.deleteByPrimaryKey(id);

			//删除规格表选项数据
            TbSpecificationOptionExample example=new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
