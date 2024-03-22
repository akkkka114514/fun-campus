package com.akkkka.funcampusmainapi.api;

import com.akkkka.funcampusmainmodel.entity.Organizer;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IOrganizerService extends IService<Organizer> {

    void addOrganizer(Organizer organizer);

    void updateOrganizer(Organizer organizer);

    void deleteOrganizer(Integer id);
}
