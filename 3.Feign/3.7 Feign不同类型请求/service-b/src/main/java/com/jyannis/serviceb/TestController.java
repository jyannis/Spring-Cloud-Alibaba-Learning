package com.jyannis.serviceb;

import org.springframework.web.bind.annotation.*;


@RestController
public class TestController {

    /**
     * GET path
     * 路径参数
     * @param argue
     * @return
     */
    @GetMapping("path/{argue}")
    public String path(@PathVariable("argue") String argue){
        return argue;
    }

    /**
     * GET query
     * 查询参数
     * @param argue
     * @return
     */
    @GetMapping("query/{argue}")
    public String query(@RequestParam("argue") String argue){
        return argue;
    }

    /**
     * GET formdata
     * form-data参数
     * @param user
     * @return
     */
    @GetMapping("form-data")
    public User formdata(User user){
        return user;
    }



    /**
     * POST path
     * 路径参数
     * @param argue
     * @return
     */
    @PostMapping("path/{argue}")
    public String pathPost(@PathVariable("argue") String argue){
        return argue;
    }

    /**
     * POST query
     * 查询参数
     * @param argue
     * @return
     */
    @PostMapping("query/{argue}")
    public String queryPost(@RequestParam("argue") String argue){
        return argue;
    }

    /**
     * POST body
     * body参数
     * @param user
     * @return
     */
    @PostMapping("body")
    public User bodyPost(@RequestBody User user){
        return user;
    }

    /**
     * POST formdata
     * form-data参数
     * @param user
     * @return
     */
    @PostMapping("form-data")
    public User formdataPost(User user){
        return user;
    }

}
