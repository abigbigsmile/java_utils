package com.smile.myUtil.AlipayUtil;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.smile.myUtil.common.ResponseMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

import static com.sun.xml.internal.ws.api.message.Packet.State.ServerResponse;

@Controller
@RequestMapping("order/")
public class PayController {

    private PayService payService = new PayService();

    @RequestMapping("pay")
    @ResponseBody
    public ResponseMessage pay(@RequestParam(name = "userId")String userId, @RequestParam(name = "orderId")String orderId){

        System.out.println("进入pay环节");
        return payService.pay(userId, orderId);

    }

    @RequestMapping("callBack")
    @ResponseBody
    public ResponseMessage callBack(HttpServletRequest request){
        System.out.println("回调");
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i = 0 ; i <values.length;i++){
                System.out.println(valueStr);
                valueStr = (i == values.length -1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }

        //非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ResponseMessage.createErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            System.out.println("支付宝验证回调异常");
        }

        return ResponseMessage.createSuccess("验证成功", params);
    }

}
