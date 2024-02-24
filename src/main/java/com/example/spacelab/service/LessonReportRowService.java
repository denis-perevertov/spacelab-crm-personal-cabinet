package com.example.spacelab.service;

import com.example.spacelab.dto.lesson.LessonReportRowSaveDTO;
import com.example.spacelab.model.lesson.LessonReportRow;

public interface LessonReportRowService {

    LessonReportRow getLessonReportRowById(Long id);
    LessonReportRow createNewLessonReportRow(LessonReportRow lessonReportRow);
    void updateLessonReportRowAndCompletedTask(LessonReportRowSaveDTO lessonReportRowSaveDTO);
    void deleteLessonReportRowById(Long id);
}
