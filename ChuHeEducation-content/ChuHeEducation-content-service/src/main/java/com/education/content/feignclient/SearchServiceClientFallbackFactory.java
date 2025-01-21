package com.education.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ClassName：SearchServiceClientFallbackFactory
 *
 * @author: Devil
 * @Date: 2025/1/21
 * @Description:
 * @version: 1.0
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引发生熔断，索引信息：{},熔点异常：{}",courseIndex,throwable.toString(),throwable);
                //走降级了，返回false
                return false;
            }
        };
    }
}
