 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,uploadsService,itemCatService,typeTemplateService,$location){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id=$location.search()['id'];
		if (id==null){
			return;
		}

		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				editor.html($scope.entity.goodsDesc.introduction);//给富文本编辑器赋值
				//商品图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);//商品图片
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);//扩展属性
                //规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//SKU列表规格列转换
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec =
                        JSON.parse( $scope.entity.itemList[i].spec);
                }
            }
		);				
	}
//新增商品信息
    $scope.save=function(){

        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction=editor.html();
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert('保存成功');
                    location.href="goods.html";
					$scope.entity={};
                    editor.html("");
                }else{
                    alert(response.message);
                }
            }
        );
    }


	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //上传图片
	$scope.uploadFile=function () {
		uploadsService.uploadFile().success(function (response) {
			if (response.success){
                $scope.image_entity.url= response.message;
			} else{
				alert(response.message);
			}
        })
    }
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}}
    //将当前上传的图片实体存入集合对象
    $scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //移除图片
    $scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    
    $scope.clearFile=function () {
        var obj = document.getElementById('file') ;
        obj.outerHTML=obj.outerHTML;
    }

    //查询一级商品分类列表
    $scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1List=response;
        })
    }

    //查询二级商品分类列表
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {

            itemCatService.findByParentId(newValue).success(function (response) {
                $scope.itemCat2List=response;
            })
    });
    //查询三级商品分类列表
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {

            itemCatService.findByParentId(newValue).success(function (response) {
                $scope.itemCat3List=response;
            })
    });
    //读取模板id
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {

        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId=response.typeId;
        })
    });
	//读取模板id后，读取品牌列表
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {

        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.typeTemplate=response;//模板对象
            $scope.typeTemplate.brandIds=JSON.parse( $scope.typeTemplate.brandIds);//品牌列表对象转换
			//扩展属性
			if ($location.search()['id']==null){//如果是增加商品
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
            }
        });

        //读取规格
        typeTemplateService.findSpecList(newValue).success(function (response) {
			$scope.specList=response;
        });
    });

    $scope.updateSpecAttribute=function ($event,name,value) {

       var object=$scope.searchObjectKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
   		if (object!=null){
   			if ($event.target.checked){
                object.attributeValue.push(value);
			}else{//取消选项
                object.attributeValue.splice(object.attributeValue.indexOf(value ) ,1);//移除选项
             	//如果选项都取消，就移除该项
             	if (object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		} else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
    }

    //创建SKU列表
    $scope.createItemList=function(){

        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'} ];//列表初始化

        var items= $scope.entity.goodsDesc.specificationItems;

        for(var i=0;i<items.length;i++){
            $scope.entity.itemList= addColumn( $scope.entity.itemList, items[i].attributeName,items[i].attributeValue );

        }

    }

    addColumn=function(list,columnName,columnValues){

        var newList=[];
        for(var i=0;i< list.length;i++){
            var oldRow=  list[i];
            for(var j=0;j<columnValues.length;j++){
                var newRow=  JSON.parse( JSON.stringify(oldRow)  );//深克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    $scope.status=['未审核','已审核','审核未通过','已关闭'];//状态列表

    $scope.itemCatList=[];//商品分类列表
    $scope.findItemCatList=function () {
		itemCatService.findAll().success(function (response) {
			for (var i=0;i<response.length;i++){
				$scope.itemCatList[response[i].id]=response[i].name;
			}
        })
    }

//根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectKey(items,'attributeName',specName);
        if(object==null){
            return false;
        }else{
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }
    }

});	
