package com.xinling.system.domain.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class FileUploadReqVO {

    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    /**
     * 文件目录
     */
    private String directory;

    @AssertTrue(message = "文件目录不正确")
    @JsonIgnore
    public boolean isDirectoryValid() {
        return !StrUtil.containsAny(directory, "..", "/", "\\");
    }

}
