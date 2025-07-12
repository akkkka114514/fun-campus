package com.akkkka.funcampusapiportal.feign;

import com.akkkka.funcampusapiportal.domain.ActivityComment;
import com.akkkka.funcampusapiportal.vo.ActivityCommentRootVO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: akkkka114514
 * @create: 2025-06-23 19:53
 * @description:
 */
@Component
public interface PortalCommentFeign {
    @PostMapping("/add")
    void add(@Valid @RequestBody ActivityComment activityComment);

    @GetMapping("/get")
    List<ActivityCommentRootVO> getByActivityId(@RequestParam Integer activityId);

    @PostMapping("/update")
    void update(@Valid @RequestBody ActivityComment activityComment);

    @GetMapping("/delete")
    void delete(@RequestParam("commentId")Integer id);
}
