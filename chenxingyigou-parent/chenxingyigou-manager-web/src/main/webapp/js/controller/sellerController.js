 //控制层 
app.controller('sellerController' ,function($scope,$controller   ,sellerService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		sellerService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		sellerService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		sellerService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=sellerService.update( $scope.entity ); //修改  
		}else{
			serviceObject=sellerService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    alert(response.message);
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		sellerService.dele( $scope.selectIds ).success(
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
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		sellerService.search(page,rows,$scope.searchEntity).success(
			function(response){
                $scope.list=response.rows;
					for (var i=0;i< $scope.list.length;i++){
					if ($scope.list[i].status==0){
                        $scope.list[i].status="待审核";
					}
					if ($scope.list[i].status==1){
                        $scope.list[i].status="已审核";
					}
					if ($scope.list[i].status==2){
                        $scope.list[i].status="审核未通过 ";
					}
                    if ($scope.list[i].status==3){
                        $scope.list[i].status="关闭商家 ";
                    }

				}
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//修改状态
	$scope.updateStatus=function (sellerId,status) {
		sellerService.updateStatus(sellerId,status).success(function (response) {
			if (response.success){
                alert(response.message);
				$scope.reloadList();
			} else
				alert(response.message);
        })
    }
});	
