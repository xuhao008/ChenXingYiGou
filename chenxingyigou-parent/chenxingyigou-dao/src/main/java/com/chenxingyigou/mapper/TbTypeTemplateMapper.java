package com.chenxingyigou.mapper;

import com.chenxingyigou.pojo.TbTypeTemplate;
import com.chenxingyigou.pojo.TbTypeTemplateExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TbTypeTemplateMapper {
    int countByExample(TbTypeTemplateExample example);

    int deleteByExample(TbTypeTemplateExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbTypeTemplate record);

    int insertSelective(TbTypeTemplate record);

    List<TbTypeTemplate> selectByExample(TbTypeTemplateExample example);

    TbTypeTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbTypeTemplate record, @Param("example") TbTypeTemplateExample example);

    int updateByExample(@Param("record") TbTypeTemplate record, @Param("example") TbTypeTemplateExample example);

    int updateByPrimaryKeySelective(TbTypeTemplate record);

    int updateByPrimaryKey(TbTypeTemplate record);

    List<Map> findByTemplate();
}