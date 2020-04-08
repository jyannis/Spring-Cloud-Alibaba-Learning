package com.jyannis.servicea.feignclient;

import com.jyannis.servicea.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 注解@FeignClient指定该类负责对service-b服务的远程调用
 */
@FeignClient(name = "service-b")
public interface ServiceBFeignClient {

    /**
     * GET path
     * 路径参数
     * @param argue
     * @return
     */
    @GetMapping("path/{argue}")
    public String path(@PathVariable("argue") String argue);

    /**
     * GET query
     * 查询参数
     * @param argue
     * @return
     */
    @GetMapping("query/{argue}")
    public String query(@RequestParam("argue") String argue);

    /**
     * 传实体类型时需要加上@SpringQueryMap
     * GET formdata
     * form-data参数
     * @param user
     * @return
     */
    @GetMapping("form-data")
    public User formdata(@SpringQueryMap User user);



    /**
     * POST path
     * 路径参数
     * @param argue
     * @return
     */
    @PostMapping("path/{argue}")
    public String pathPost(@PathVariable("argue") String argue);

    /**
     * POST query
     * 查询参数
     * @param argue
     * @return
     */
    @PostMapping("query/{argue}")
    public String queryPost(@RequestParam("argue") String argue);

    /**
     * POST body
     * body参数
     * @param user
     * @return
     */
    @PostMapping("body")
    public User bodyPost(User user);

    /**
     * POST formdata
     * form-data参数
     * @param user
     * @return
     */
    @PostMapping("form-data")
    public User formdataPost(@SpringQueryMap User user);

}
