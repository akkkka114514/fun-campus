package com.akkkka.funcampusportal.service;

import com.akkkka.funcampusportal.domain.Organizer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: Zoe Wu
 * @create: 2025-03-07 21:53
 * @Description:
 */
public interface IOrganizerService extends IService<Organizer> {
    void add(Organizer organizer);

    void update(Organizer organizer);

    void delete(Integer id);
}
