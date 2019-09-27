app.controller('searchController',function($scope,searchService,$location){
    //定义搜索对象的属性
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{ },'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''}

    //搜索
    $scope.search=function(){
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap=response;
                //构建分类栏
                $scope.buildPageLabel();
            }
        );
    }

    //构建分类栏
    $scope.buildPageLabel=function(){
        //构建分类栏
        $scope.pageLabel=[];
        //开始页码
        var firstPage=1;
        //结束页码
        var lastPage=$scope.resultMap.totalPages;
        //开始有点
        $scope.firstDot=true;
        //结束有点
        $scope.lastDot=true;
        if ($scope.resultMap.totalPages>5){//如果页码数量大于5
            if ($scope.searchMap.pageNo<=3){//如果当前页面小于等于3，显示前5页
                lastPage=5;
                $scope.firstDot=false;
            }else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
                firstPage=$scope.resultMap.totalPages-4;
                $scope.lastDot=false;
            }
            else{
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        for (var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }

    }

    //添加搜索项，改变searchMap的值
    $scope.addSearchItem=function (key,value) {
        if (key=='category'||key=='brand'||key=='price'){//如果用户点击的是分类或品牌
            $scope.searchMap[key]=value;
        }else{//否则是规格
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();//查询
    };

    //撤销搜索项
    $scope.removeSearchItem=function(key){
        if(key=="category" ||  key=="brand"||key=="price"){//如果是分类或品牌
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
    }

    $scope.queryByPage=function (pageNo) {
        if (pageNo<1||pageNo>$scope.searchMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }
    //判断当前页是否为第一页
    
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNo==1){
            return true;
        } else{
            return false;
        }
    }
    //判断当前页是否为最后一页
    $scope.idEndPage=function () {
        if ($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        } else{
            return false;
        }

    };

    $scope.con='综合';
    $scope.actives=function(con){
        if ($scope.con==con){
            return true;
        }else{
            return false;
        }
    }

    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();//查询
    };
    
    //判断关键字和品牌的关系
    $scope.keywordsIsBrand=function () {

        for (var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    //判断关键字和分类的关系
    $scope.keywordsIscategory=function () {
        for (var i=0;i<$scope.resultMap.categoryList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.categoryList[i])>=0){
                return true;
            }
        }
        return false;
    }

    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }
});