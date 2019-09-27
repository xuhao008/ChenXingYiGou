var app=angular.module('chenxingyigou',[]);

//对应过滤器
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {//传入参数是被过滤的内容
        return $sce.trustAsHtml(data);//返回的是过滤后的内容（信任html转换）
    }
}])