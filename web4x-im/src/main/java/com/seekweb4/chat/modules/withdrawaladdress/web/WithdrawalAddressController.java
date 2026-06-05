package com.seekweb4.chat.modules.withdrawaladdress.web;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.modules.withdrawaladdress.entity.WithdrawalAddress;
import com.seekweb4.chat.modules.withdrawaladdress.service.WithdrawalAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/withdrawal/address")
public class WithdrawalAddressController {

    @Autowired
    private WithdrawalAddressService withdrawalAddressService;

    /**
     * 查询某个用户的提币地址列表（只返回未删除、正常状态）
     */
    @ApiLog("查询用户提币地址列表")
    @GetMapping(value = "listByUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson listByUser(@RequestParam("userId") String userId,
                               @RequestParam(value = "addressType", required = false) Integer addressType) {
        List<WithdrawalAddress> list = withdrawalAddressService.findByUser(userId, addressType);
        return AjaxJson.success().put("list", list);
    }

    /**
     * 新增提币地址
     */
    @ApiLog("新增提币地址")
    @PostMapping(value = "add", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson add(@RequestBody WithdrawalAddress address) {
        if (address.getUserId() == null || address.getUserId().trim().isEmpty()) {
            return AjaxJson.error("userId 不能为空");
        }
        if (address.getToAddress() == null || address.getToAddress().trim().isEmpty()) {
            return AjaxJson.error("地址不能为空");
        }
        withdrawalAddressService.addAddress(address);
        return AjaxJson.success("新增成功");
    }

    /**
     * 修改提币地址
     */
    @ApiLog("修改提币地址")
    @PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson update(@RequestBody WithdrawalAddress address) {
        if (address.getId() == null) {
            return AjaxJson.error("id 不能为空");
        }
        withdrawalAddressService.updateAddress(address);
        return AjaxJson.success("更新成功");
    }

    /**
     * 设置默认提币地址
     */
    @ApiLog("设置默认提币地址")
    @PostMapping(value = "setDefault", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson setDefault(@RequestParam("id") Integer id) {
        withdrawalAddressService.setDefault(id);
        return AjaxJson.success("设置默认成功");
    }

    /**
     * 删除提币地址（软删除）
     */
    @ApiLog("删除提币地址")
    @DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson delete(@RequestParam("id") Integer id) {
        withdrawalAddressService.delete(id);
        return AjaxJson.success("删除成功");
    }

    /**
     * 将提币地址加入黑名单
     */
    @ApiLog("拉黑提币地址")
    @PostMapping(value = "blacklist", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson blacklist(@RequestParam("id") Integer id) {
        withdrawalAddressService.blacklist(id);
        return AjaxJson.success("已加入黑名单");
    }
}

