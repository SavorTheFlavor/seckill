<!DOCTYPE html>
<html>
<head>
    <title>Kill detail...</title>
    <meta charset="utf-8">
    <%@include file="common/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="pannel-heading h1">${seckill.name}</div>
        </div>
        <div class="panel-body">
            <h2 class="text-danger">
                <!--time icon-->
                <span class="glyphicon glyphicon-time"></span>
                <!--count down-->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
<!-- login in , input number-->
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <!--phone icon-->
                    <span class="glyphicon glyphicon-phone"></span>phone:
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey"
                        placeholder="input phone~@~:" class="form-control"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <!--vertify information....-->
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneBtn" class="btn btn-success">
                        <!--phone icon-->
                        <span class="glyphicon glyphicon-phone"></span>
                        Submit
                    </button>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
<!--使用cdn获取公共js http://www.bootcdn.cn/ -->
<!--JQuery cookie -->
<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!--JQuery count down -->
<script src="http://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<!--主要逻辑,引入seckill对象-->
<script src="/resources/script/seckill.js"></script>
<script type="text/javascript">
    $(function(){
       //使用EL表达式获取参数并传入
        seckill.detail.init({
               seckillId:${seckill.seckillId},
               startTime:${seckill.startTime.time},
               endTime:${seckill.endTime.time}
           }
       );
    });
</script>
</html>