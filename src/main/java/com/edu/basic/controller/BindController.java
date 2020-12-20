package com.edu.basic.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.edu.basic.bind.BindFactory;
import com.edu.basic.bind.BindModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/addBind")
public class BindController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private int barcodeIndex = 1;
    private int imageIndex = 1;

    @GetMapping("now")
    @ResponseBody
    public String getDate(){
        return DateUtil.formatDate(new Date());
    }

    @GetMapping("showBinds")
    @ResponseBody
    public String showBinds(){
        for (List<BindModel> bindModels : BindFactory.bindSuccessList) {
            BindModel barCode = bindModels.get(0);
            BindModel image = bindModels.get(1);
            logger.info("绑定成功 barCode:{} time:{} image:{} time:{}",
                    barCode.getBarCode(),
                    DateUtil.formatDateTime(barCode.getCreateTime()),
                    image.getImageId(),
                    DateUtil.formatDateTime(image.getCreateTime()));
        }
        for (BindModel bindModel : BindFactory.unbindBarcode) {
            logger.info("有图片无条码:{} - {} ",bindModel.getImageId(),bindModel.getCreateTime());
        }
        for (BindModel bindModel : BindFactory.unbindImage) {
            logger.info("有条码无图片:{} - {} ",bindModel.getBarCode(),bindModel.getCreateTime());
        }

        for (BindModel bindModel : BindFactory.unmatchModel) {
            logger.info("未匹配上的数据：{}",JSON.toJSONString(bindModel));
        }
        return "success";
    }

    @PostMapping("/bc")
    @ResponseBody
    public String addBarCode(String date) throws InterruptedException {
        Date now = new Date();
        BindModel bindModel = new BindModel();
        bindModel.setBarCode("bc_00"+barcodeIndex);
        barcodeIndex += 1;
        bindModel.setCreateTime(DateUtil.parseDateTime(date));
        bindModel.setBindStatus(BindModel.STATUS_WAIT_BIND);
        BindFactory.barCodeQueue.put(bindModel);
        logger.info("[添加条码] queue size{} content:{}",BindFactory.barCodeQueue.size(),JSON.toJSONString(BindFactory.barCodeQueue));
        return JSON.toJSONString(bindModel);
    }

    @PostMapping("/img")
    @ResponseBody
    public String addImage(String date) throws InterruptedException {
        Date now = new Date();
        BindModel bindModel = new BindModel();
        bindModel.setImageId("img_00"+imageIndex);
        imageIndex += 1;
        bindModel.setCreateTime(DateUtil.parseDateTime(date));
        bindModel.setBindStatus(BindModel.STATUS_WAIT_BIND);
        BindFactory.imageQueue.put(bindModel);
        logger.info("[添加图片] queue size{} content:{}",BindFactory.imageQueue.size(),JSON.toJSONString(BindFactory.imageQueue));
        return JSON.toJSONString(bindModel);
    }

    @PostMapping("/all")
    @ResponseBody
    public String addAll(String bcDate,String imgDate) throws InterruptedException {
        Date now = new Date();
        BindModel bindModel = new BindModel();
        bindModel.setBarCode("bc_00"+barcodeIndex);
        bindModel.setCreateTime(DateUtil.parseDateTime(bcDate));
        bindModel.setBindStatus(BindModel.STATUS_WAIT_BIND);
        BindFactory.barCodeQueue.put(bindModel);
        logger.info("[添加条码] queue size{} content:{}",BindFactory.barCodeQueue.size(),JSON.toJSONString(BindFactory.barCodeQueue));

        bindModel = new BindModel();
        bindModel.setImageId("img_00"+imageIndex);
        bindModel.setCreateTime(DateUtil.parseDateTime(imgDate));
        bindModel.setBindStatus(BindModel.STATUS_WAIT_BIND);
        BindFactory.imageQueue.put(bindModel);
        logger.info("[添加图片] queue size{} content:{}",BindFactory.imageQueue.size(),JSON.toJSONString(BindFactory.imageQueue));
        barcodeIndex += 1;
        imageIndex += 1;
        return JSON.toJSONString(bindModel);
    }

}
