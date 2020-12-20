package com.edu.basic.bind;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class BindThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void run() {
        try {
            BindModel image = BindFactory.imageQueue.poll();
            BindModel barCode = BindFactory.barCodeQueue.poll();
            //logger.info("bindThread begin :{} ",DateUtil.formatDateTime(new Date()));
            if (image == null && barCode == null) {
                // logger.info("bindThread return end :{} ",DateUtil.formatDateTime(new Date()));
                return;
            }

            if (image == null) {
                logger.info("[bind] 阻塞获取图片开始");
                image = pullImage();
                logger.info("[bind] 阻塞获取图片结束:{} - {} queueSize:{}", JSON.toJSONString(image), DateUtil.formatDateTime(new Date()), BindFactory.imageQueue.size());
            }

            if (barCode == null) {
                logger.info("[bind] 阻塞获取条码开始");
                barCode = pullBarCode();
                logger.info("[bind] 阻塞获取条码结束:{}-{} queueSize:{}", JSON.toJSONString(barCode), DateUtil.formatDateTime(new Date()), BindFactory.barCodeQueue.size());
            }
            check(barCode, image);
        }catch (Exception e){
            logger.error("异常了",e);
        }
       // logger.info("bindThread success end :{} ",DateUtil.formatDateTime(new Date()));
    }


    private void check(BindModel barCode,BindModel image){
        logger.info("[bind start] image:{}  barCode:{}"          , JSON.toJSONString(image), JSON.toJSONString(barCode));
        if(barCode == null){
            logger.info("[bind] 有图片无条码 图片:{}", image.getImageId());
            BindFactory.unbindBarcode.add(image);
        }else if(image == null){
            logger.info("[bind] 有条码无图片 条码:{}",barCode.getBarCode());
            BindFactory.unbindImage.add(barCode);
        } else {
            if(DateUtil.between(barCode.getCreateTime(),image.getCreateTime(), DateUnit.SECOND) > 20){
                //产生时间超过20秒
                if(barCode.getCreateTime().after(image.getCreateTime())){
                    //图片在条码生成前20秒 抛弃 并且获取下一张图片
                    logger.info("[bind] 抛弃图片 :{}",JSON.toJSONString(image));
                    BindFactory.unmatchModel.add(image);
                    image = pullImage();
                    check(barCode,image);
                }else {
                    //条码在图片生成前20秒  抛弃 并获取下一个条码结果
                    logger.info("[bind] 抛弃条码 :{}",JSON.toJSONString(barCode));
                    BindFactory.unmatchModel.add(barCode);
                    //计算阻塞时间
                    barCode = pullBarCode();
                    check(barCode,image);
                }
                return;
            }
            BindFactory.bindSuccessList.add(Arrays.asList(barCode,image));
        }
    }

    public  BindModel pullImage(){
        try {
            return BindFactory.imageQueue.poll(20L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("[bind] getImage error {}",e.getMessage());
        }
        return null;
    }

    public BindModel pullBarCode(){
        try {
            return BindFactory.barCodeQueue.poll(20L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("[bind] getBarCode error {}",e.getMessage());
        }
        return null;
    }




}
