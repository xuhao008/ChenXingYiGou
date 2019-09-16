//创建控制器,$scope是控制层和视图层交流数据的桥梁
app.controller("brandController",function($scope,$http,brandService, $controller){

    $controller('baseController',{$scope:$scope});

    //查询品牌列表
    /*$scope.findAll=function () {
        brandService.findAll().success(function (response) {
             $scope.list=response;
         })
     }*/

    //分页
    /*  $scope.findPage=function(page,size){
          $http.get('../brand/findPage.do?page='+page+'&size='+size).success(
              function(response){
                  $scope.list=response.rows;//显示当前页的数据
                  $scope.paginationConf.totalItems=response.total;//更新总记录数
              }
          );
      }*/

    //新增，修改
    $scope.save=function () {
        var object=null;//方法名
        if ($scope.entity.id!=null){
            object=brandService.update($scope.entity);
        }else{
            object=brandService.add($scope.entity);
        }
        object.success(function (response) {
            if (response.success){
                alert(response.message);
                $scope.reloadList();//重新加载
            }else {
                alert(response.message);
            }
        })
    }
    //根据id查询品牌信息
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        })
    }

    //删除
    $scope.del=function () {
        brandService.del($scope.selectIds).success(function (response) {
            if (response.success){
                alert(response.message);
                $scope.reloadList();//重新加载
            } else {
                alert(response.message);
            }
        })
    }

    $scope.searchEntity={};
    //模糊查询
    $scope.search=function (page,size) {
        brandService.search(page,size,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;//显示当前页的数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }
});
