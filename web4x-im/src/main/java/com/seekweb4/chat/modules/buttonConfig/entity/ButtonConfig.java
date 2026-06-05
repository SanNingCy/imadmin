package com.seekweb4.chat.modules.buttonConfig.entity;

import com.seekweb4.chat.core.persistence.BaseEntity;
import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 按钮配置表
 *
 * @author system
 * @since 2025-11-28
 */
//@Data
public class ButtonConfig extends DataEntity<ButtonConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 内部Long类型ID（用于数据库映射）
     */
    private Long longId;

    /**
     * 按钮标识(transfer:转账 withdraw:提现)
     */
    @NotBlank(message = "按钮标识不能为空")
    @ExcelField(title = "按钮标识", align = 2, sort = 1)
    private String buttonKey;

    /**
     * 按钮名称
     */
    @NotBlank(message = "按钮名称不能为空")
    @ExcelField(title = "按钮名称", align = 2, sort = 2)
    private String buttonName;

    /**
     * 按钮状态(0:关闭 1:开启)
     */
    @NotNull(message = "按钮状态不能为空")
    @ExcelField(title = "按钮状态", align = 2, sort = 3, dictType = "button_status")
    private Integer buttonStatus;

    /**
     * 重写getId方法，将Long id转换为String
     */
    @Override
    public String getId() {
        if (longId != null) {
            String idStr = String.valueOf(longId);
            super.setId(idStr); // 同步到父类的id字段
            return idStr;
        }
        return super.getId();
    }

    /**
     * 重写setId方法，将String id转换为Long
     */
    @Override
    public void setId(String id) {
        super.setId(id);
        if (StringUtils.isNotBlank(id)) {
            try {
                this.longId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                this.longId = null;
            }
        } else {
            this.longId = null;
        }
    }

    /**
     * 获取创建人ID（字符串形式）
     */
    public String getCreateById() {
        if (getCreateBy() != null && getCreateBy().getId() != null) {
            return getCreateBy().getId();
        }
        return null;
    }

    /**
     * 设置创建人ID（字符串形式）
     */
    public void setCreateById(String createById) {
        if (StringUtils.isNotBlank(createById)) {
            com.seekweb4.chat.modules.sys.entity.User user = new com.seekweb4.chat.modules.sys.entity.User();
            user.setId(createById);
            setCreateBy(user);
        }
    }

    /**
     * 获取更新人ID（字符串形式）
     */
    public String getUpdateById() {
        if (getUpdateBy() != null && getUpdateBy().getId() != null) {
            return getUpdateBy().getId();
        }
        return null;
    }

    /**
     * 设置更新人ID（字符串形式）
     */
    public void setUpdateById(String updateById) {
        if (StringUtils.isNotBlank(updateById)) {
            com.seekweb4.chat.modules.sys.entity.User user = new com.seekweb4.chat.modules.sys.entity.User();
            user.setId(updateById);
            setUpdateBy(user);
        }
    }

    public ButtonConfig() {
        super();
        this.setIdType(BaseEntity.IDTYPE_AUTO); // 设置为自增主键
    }

    public ButtonConfig(String id) {
        super(id);
        this.setIdType(BaseEntity.IDTYPE_AUTO);
    }

    public Long getLongId() {
        return longId;
    }

    public void setLongId(Long longId) {
        this.longId = longId;
    }

    public String getButtonKey() {
        return buttonKey;
    }

    public void setButtonKey(String buttonKey) {
        this.buttonKey = buttonKey;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public Integer getButtonStatus() {
        return buttonStatus;
    }

    public void setButtonStatus(Integer buttonStatus) {
        this.buttonStatus = buttonStatus;
    }
}

