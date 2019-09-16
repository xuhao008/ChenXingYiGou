package com.chenxingyigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenxingyigou.pojo.TbBrand;
import com.chenxingyigou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌信息
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){

        return brandService.findAll();
    }

    /**
     * 分页查询品牌信息,被模糊查询替代
     * @param page
     * @param size
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int size){
        return  brandService.findPage(page,size);
    }

    /**
     * 新增品牌信息
     * @param brand
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            Boolean con = brandService.add(brand);
            if (con==true){
                return new Result(true,"保存成功！");
            }else {
                return new Result(false,"保存失败，该品牌已存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败，服务器异常，请稍后再试！");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
      return  brandService.findOne(id);
    }

    @RequestMapping("/update")
    public  Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败，服务器异常，请稍后再试！");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败，服务器异常，请稍后再试！");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand,int page,int size){
       return brandService.findPage(tbBrand,page,size);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
