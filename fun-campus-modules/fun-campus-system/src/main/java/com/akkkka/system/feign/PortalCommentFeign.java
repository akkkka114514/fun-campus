package com.akkkka.system.feign;

import com.akkkka.funcampusportal.domain.ActivityComment;
import com.akkkka.funcampusportal.util.ParamCheckUtil;
import com.akkkka.funcampusportal.vo.ActivityCommentRootVO;
import io.swagger.v3.oas.annotations.Operation;
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
