//主要交互逻辑
    //js模块化
var seckill={
    //封装秒杀相关的ajax的url
    URL:{
        now:function () {
            return '/seckill/time/now'
        },
        exposer:function(seckillId){
            return '/seckill/'+seckillId+'/exposer';
        },
        execution:function(md5,seckillId){
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    //详情页秒杀逻辑
    validatePhone:function (phone) {
        if(phone && phone.length == 11 && !isNaN(phone)){
            return true;
        }
        return false;
    },
    countdown:function(seckillId,startTime,endTime,nowTime){
        var seckillBox = $('#seckill-box');
        if(nowTime >= endTime){//seckill was over
            seckillBox.html('It was over!');
        }else if(nowTime < startTime){// seckill hasn't been opened yet!
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime,function(event){
                var format = event.strftime('seckill beginning:%Dday %Hhour  %Mminute %Ssecond');
                seckillBox.html(format);
            }).on('finish.countdown',function () {
                //获取秒杀地址，控制显示逻辑,执行秒杀
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        }else {//seckill is going on
            seckill.handleSeckillkill(seckillId,seckillBox);
        }
    },
    handleSeckillkill:function(seckillId,node){
        //获取秒杀地址，控制显示逻辑,执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">Kill!!!!</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //回调函数中执行交互逻辑

            if(result&&result['success']){
                var exposer = result['data'];
                if(exposer['exposed']){//秒杀开始
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(md5,seckillId);
                    console.log('killUrl:'+killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click',function(){
                        //执行秒杀请求
                        //先禁用按钮
                        $(this).addClass('disabled');
                        //发送秒杀请求
                        $.post(killUrl,{},function(result){
                            if(result&&result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                   node.show();
                }else {//还未开始，不同的pc机的计时偏差导致的
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //让其继续执行countdown
                    seckill.countdown(seckillId,start,end,now);
                }
            }else {
               console.log('result:'+result);
            }
        });
    },
    detail:{
        init:function (params) {
            //手机验证和登录，计时交互

            //cookie中查找手机号,懒得弄用户登录
            var killPhone = $.cookie('killPhone');

            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定手机号
                var killPhoneModal = $('#killPhoneModal');
                //弹出层
                killPhoneModal.modal({
                    show:true,//显示
                    backdrop:'static',//禁止位置关闭
                    keyboard:false
                });
                $('#killPhoneBtn').click(function(){
                    var inputPhone = $('#killPhoneKey').val();
                    if(seckill.validatePhone(inputPhone)){

                        //写入cookie
                        $.cookie('killPhone',inputPhone,{expires:1,path:'seckill'});
                        //刷新页面
                        window.location.reload();
                    }else {
                        //先隐藏一下.....等内容放好后..
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">phone error!</label>').show(500);
                    }});
            }
            //has logined in
            //count down if it haven't started
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(),{},function (result) {
                if(result&&result['success']){
                    var nowTime = result['data'];
                    seckill.countdown(seckillId,startTime,endTime,nowTime);
                }

            })

        }
    }
    
}