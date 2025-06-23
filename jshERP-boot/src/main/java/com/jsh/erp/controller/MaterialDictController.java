package com.jsh.erp.controller;

import com.jsh.erp.base.BaseController;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/materialDict")
@Api(tags = {"商品字典管理"})
public class MaterialDictController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(MaterialDictController.class);

    @GetMapping(value = "/getDictItems")
    @ApiOperation(value = "根据字典类型获取字典项列表")
    public BaseResponseInfo getDictItems(@RequestParam("typeCode") String typeCode,
                                         HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 模拟返回字典项数据，实际应该从数据库查询
            List<Map<String, Object>> list = new ArrayList<>();

            if ("base_material".equals(typeCode)) {
                list.add(createDictItem("copper", "铜胎"));
                list.add(createDictItem("silver", "银胎"));
                list.add(createDictItem("iron", "铁胎"));
                list.add(createDictItem("gold", "金胎"));
            } else if ("accessory".equals(typeCode)) {
                list.add(createDictItem("necklace", "项链"));
                list.add(createDictItem("bracelet", "手镯"));
                list.add(createDictItem("earring", "耳环"));
                list.add(createDictItem("ring", "戒指"));
            } else if ("packaging".equals(typeCode)) {
                list.add(createDictItem("gift_box", "礼盒装"));
                list.add(createDictItem("simple", "简装"));
                list.add(createDictItem("custom", "定制装"));
                list.add(createDictItem("luxury", "豪华装"));
            }

            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取字典项失败";
        }
        return res;
    }

    private Map<String, Object> createDictItem(String code, String name) {
        Map<String, Object> item = new HashMap<>();
        item.put("value", code);
        item.put("text", name);
        item.put("title", name);
        return item;
    }

    @GetMapping(value = "/getAllDictTypes")
    @ApiOperation(value = "获取所有字典类型")
    public BaseResponseInfo getAllDictTypes(HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 模拟返回字典类型数据
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(createDictType("base_material", "底胎材质"));
            list.add(createDictType("accessory", "配饰"));
            list.add(createDictType("packaging", "包装装裱"));

            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取字典类型失败";
        }
        return res;
    }

    private Map<String, Object> createDictType(String code, String name) {
        Map<String, Object> type = new HashMap<>();
        type.put("typeCode", code);
        type.put("typeName", name);
        return type;
    }



}
