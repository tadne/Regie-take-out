package com.itheima.controller;

import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;


@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController
{
    @Value("${regie.path}")
    private String basePath;
    /*
     * 文件上传:upload:
     * method=post                        对页面的 form 表单要求:
     * enctype="multipart/form=data"      采用 multipart 格式上传
     * type="file                         使用 input 的 file 控件上传
     *
     *一般使用 commons-fileupload 和 commons-io 来处理
     *
     * 在 spring 框架中封装了这个功能,
     * 只用在 Controller 方法中
     * 声明 MultipartFile 类型的参数
     * 即可接收上传的文件
     *
     *
     * */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)
    {
        log.info(file.toString());
        //file是一个临时文件,需要转存到指定为止,否则在请求完成后就会删除

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //为了避免文件名重复,就要使用 uuid 文件名唯一
        String fileName = UUID.randomUUID().toString()+suffix;
        //判断basePath目录结构是否存在,如果不存在就创建
        File dir=new File(basePath);
        if (!dir.exists()){
            dir.mkdir();
        }
        try
        {
            //临时文件转存
            file.transferTo(new File(basePath+fileName));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }





    /*
    * 文件下载
    * 两种:附件下载和直接在浏览器打开
    * 页面用 img 标签 发请求,然后用流来转存
    * */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try
        {

            //读取文件内容
            FileInputStream fis=new FileInputStream(new File(basePath+name));
            //写回浏览器,在浏览器展示图片
            ServletOutputStream sos=response.getOutputStream();

            response.setContentType("image/jpeg");

            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=fis.read(bytes))!=-1){
                sos.write(bytes,0,len);
                sos.flush();
            }
            //关闭资源
            sos.close();
            fis.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }


    }







}
