app.controller('baseController',function($scope) {
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,//当前页
        totalItems: 0,//总记录数
        itemsPerPage: 10,//每页记录数
        perPageOptions: [10, 20, 30, 40, 50],//分页选项
        onChange: function(){//当页码变更后，自动触发的方法
            $scope.reloadList();//重新加载
        }
    };

    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        $scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    $scope.selectIds=[];//用户选择删除品牌的id集合
    $scope.updateSelection=function ($event,id) {
        if ( $event.target.checked){
            $scope.selectIds.push(id);//push()方法向集合中添加id
        } else{
            var index=$scope.selectIds.indexOf(id);//查找值的位置
            $scope.selectIds.splice(index,1);//参数1：移除的位置，参数2：移除的个数
        }
    }
    //json数据提取
    $scope.jsonToString=function (jsonString,key) {
        var json=JSON.parse(jsonString);
        var value="";
        for (var i=0;i<json.length;i++){
            if (i>0){
                value+=",";
            }
          value+=json[i][key];
        }
        return value;
    }

})