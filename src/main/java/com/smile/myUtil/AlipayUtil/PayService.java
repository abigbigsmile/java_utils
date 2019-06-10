package com.smile.myUtil.AlipayUtil;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.smile.myUtil.common.ResponseMessage;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PayService {

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    static {

        Configs.init("zfbinfo.properties");

        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    public ResponseMessage pay(String userId, String orderId) {

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderId;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "Smile扫码支付，订单号：" + orderId;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = "20";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "让优秀成为一种习惯！！！";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";


        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
       GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "小面包", 1000, 1);
       goodsDetailList.add(goods1);


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                                .setNotifyUrl("http://rqensf.natappfree.cc/myUtil/order/callBack")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);

        switch (result.getTradeStatus()) {
            case SUCCESS:
                //log.info("支付宝预下单成功: )");

                System.out.println("可以准备付款啦！！！");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                String path = "C:\\Users\\Smile\\Desktop\\image";
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(path + "\\qr-%s.png", response.getOutTradeNo());
                //获取二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                return ResponseMessage.createSuccess(filePath);
                //log.info("filePath:" + filePath);

            case FAILED:
                //log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                //log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                //log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ResponseMessage.createErrorMessage("获取支付宝二维码失败");
    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            //log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                //log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(), response.getSubMsg();
            }
            //log.info("body:" + response.getBody());
        }
    }






}
