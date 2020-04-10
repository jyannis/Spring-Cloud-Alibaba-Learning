# 目标

1. 使用*Feign*实现上传文件。






# 流程

1. 为*service-b*添加一个*upload*接口，以接收文件上传：

   这里为了简化业务逻辑，同时确保我们的文件上传成功了，所以返回一个文件名
   
   ```java
   @RestController
   public class TestController {
   
       /**
        * 上传文件
        * @param file
        * @return
        */
       @PostMapping("/upload")
       public String upload(@RequestPart("file") MultipartFile file){
           return file.getOriginalFilename();
       }
   
   }
   
   ```
   
   
   
2. 为*service-a*添加一个*ServiceBFeignClient*：

   ```java
   /**
    * 注解@FeignClient指定该类负责对service-b服务的远程调用
    */
   @FeignClient(name = "service-b",configuration = ServiceBFeignClient.MultipartSupportConfig.class)
   public interface ServiceBFeignClient {
   
       /**
        * 上传文件
        * @param file
        * @return
        */
       @PostMapping(value = "/upload",
               produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
               consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
       @ResponseBody
       public String upload(@PathVariable("file") MultipartFile file);
   
       class MultipartSupportConfig{
           @Bean
           public Encoder feignFormEncoder(){
               return new SpringFormEncoder();
           }
       }
   
   }
   ```

   我们发现比之前调用一般请求的FeignClient多了一些配置信息。这些配置信息是必须的，涉及到一些编码、类型转换的问题，这里就不多解释了。重点在于这是我们使用Feign远程调用上传文件的解决方案，留作demo即可。



3. 为*service-a*写一个测试接口：、

   ```java
   @RestController
   @Slf4j
   public class RemotingController {
   
       @Autowired
       private ServiceBFeignClient serviceBFeignClient;
   
       @PostMapping("remote")
       public String remote(@RequestPart("file") MultipartFile file){
           return serviceBFeignClient.upload(file);
       }
   
   }
   ```

   



# 测试

1. 启动 *Nacos*

2. 启动*service-a*，*service-b*

3. 调用*service-a*下的*remote*服务，传一个文件名为*test.png*的图片（读者可以借助postman等接口测试工具来模拟调用服务）：

   ```
   test.png
   ```

   

