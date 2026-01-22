package com.xinling.system.domain.vo;


public class FilePresignedUrlRespVO {

    /** 配置编号 */
    private Long configId;

    /**
     * 上传 uploadUrl
     *
     * 前端上传文件时，需要使用该 URL 进行上传
     */
    private String uploadUrl;

    /**
     * 文件访问 URL
     *
     * 前端上传完文件后，需要使用该 URL 进行访问
     */
    private String url;

    /**
     * 文件路径
     *
     * 前端上传完文件后，需要调用 createFile 记录下 path 路径
     */
    private String path;

}
