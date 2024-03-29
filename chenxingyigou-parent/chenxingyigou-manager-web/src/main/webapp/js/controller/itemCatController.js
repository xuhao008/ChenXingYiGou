 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}


    //当前级别
    $scope.grade=1;


	//保存 
	$scope.save=function(){
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
		serviceObject=itemCatService.update( $scope.entity,$scope.grade ); //修改
		}else{
            $scope.entity.parentId=$scope.parentId;//赋予上级ID
			if ($scope.grade==2){
                $scope.entity.typeId=$scope.entity_1.typeId;
			}
			if ($scope.grade==3){
                $scope.entity.typeId=$scope.entity_2.typeId;
			}
            serviceObject=itemCatService.add( $scope.entity );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    alert(response.message);
					//重新查询 
                    $scope.findByParentId($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    alert(response.message);
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}else{
                    alert(response.message);
				}
			}		
		);				
	}

    $scope.parentId=0;//上级ID
	//根据上级id查询分类信息
	$scope.findByParentId=function (parentId) {
        $scope.parentId=parentId;//记住上级ID
		itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list=response;
        })
    }


	//设置级别
	$scope.setGrade=function (value) {
        $scope.grade=value;
    }

    $scope.selectList=function (entity_x) {
		if ($scope.grade==1){
			$scope.entity_1=null;
            $scope.entity_2=null;
		}
        if ($scope.grade==2){
            $scope.entity_1=entity_x;
            $scope.entity_2=null;
        }
        if ($scope.grade==3){
            $scope.entity_2=entity_x;
        }
        $scope.findByParentId(entity_x.id);
    }

    $scope.template={data:[]};
	$scope.findByTypeTemplate=function () {
        typeTemplateService.findByTemplate().success(function (response) {
			$scope.template={data:response};
        })
    }

});
