app.controller('indexController',function ($scope,loginService) {

    $scope.indexLoginName=function () {
        loginService.loginName().success(function (response) {
            $scope.loginName=response.loginName;
        })
    }
})