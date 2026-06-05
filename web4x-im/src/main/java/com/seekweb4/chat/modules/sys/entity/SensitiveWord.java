package com.seekweb4.chat.modules.sys.entity;

import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import com.seekweb4.chat.core.persistence.DataEntity;
import lombok.Data;

@Data
public class SensitiveWord extends DataEntity<SensitiveWord> {

    private static final long serialVersionUID = 1L;

    @ExcelField(title = "敏感词", align = 2, sort = 1)
    private String word;

    public SensitiveWord() {
        super();
    }

    public SensitiveWord(String id) {
        super(id);
    }
}
