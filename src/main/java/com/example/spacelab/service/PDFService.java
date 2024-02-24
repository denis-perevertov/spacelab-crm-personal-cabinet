package com.example.spacelab.service;

import com.example.spacelab.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

public interface PDFService {

    File generatePDF(Task task, Locale locale) throws IOException, URISyntaxException;

}
