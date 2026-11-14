package com.placementtracker.service;

import java.util.List;

import com.placementtracker.dao.InterviewDAO;
import com.placementtracker.model.Interview;
import com.placementtracker.model.InterviewReport;

public class InterviewService {

    private InterviewDAO dao = new InterviewDAO();

    // ADD

    public boolean scheduleInterview(Interview interview) {

        return dao.scheduleInterview(interview);

    }

    // VIEW

    public List<Interview> getAllInterviews() {

        return dao.getAllInterviews();

    }

    // UPDATE

    public boolean updateInterview(int id, String result) {

        return dao.updateInterview(id, result);

    }

    // DELETE

    public boolean deleteInterview(int id) {

        return dao.deleteInterview(id);

    }

    // SEARCH

    public Interview searchInterview(String companyName) {

        return dao.searchInterview(companyName);

    }

    // FILTER

    public List<Interview> filterResult(String result) {

        return dao.filterResult(result);

    }

    // REPORT

    public InterviewReport getReport() {

        return dao.getReport();

    }

}