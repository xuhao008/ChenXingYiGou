package com.chenxingyigou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenxingyigou.content.service.ContentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.chenxingyigou.mapper.TbContentMapper;
import com.chenxingyigou.pojo.TbContent;
import com.chenxingyigou.pojo.TbContentExample;
import com.chenxingyigou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());//清空缓存
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
	    //查询原来的分组id
        Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        //清除原分组的缓存
        redisTemplate.boundHashOps("content").delete(categoryId);
        contentMapper.updateByPrimaryKey(content);
        if (categoryId.longValue()!=content.getCategoryId().longValue()) {
            //清除现分组缓存
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
            Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();//查询id
            redisTemplate.boundHashOps("content").delete(categoryId);//清除缓存
			contentMapper.deleteByPrimaryKey(id);
		}
	}


		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();

		if(content!=null){
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}

		}

		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {

        List<TbContent> list= (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
        if (list==null) {
            System.out.println("从数据库查询数据，放入缓存");
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);//指定条件，分类id
            criteria.andStatusEqualTo("1");//指定条件为启用的
            example.setOrderByClause("sort_order");//排序
            list = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("content").put(categoryId,list);//放入缓存
        }else {
            System.out.println("从缓存中查询");
        }
        return list;
	}

}
