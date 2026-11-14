package com.placementtracker.service;

import java.util.List;

import com.placementtracker.dao.AptitudeDAO;
import com.placementtracker.model.Aptitude;
import com.placementtracker.model.AptitudeReport;

public class AptitudeService {

    private AptitudeDAO dao = new AptitudeDAO();

    // ADD

    public boolean addTestResult(Aptitude aptitude) {

        return dao.addTestResult(aptitude);

    }

    // VIEW

    public List<Aptitude> getAllResults() {

        return dao.getAllResults();

    }

    // UPDATE

    public boolean updateResult(int id, String status) {

        return dao.updateResult(id, status);

    }

    // DELETE

    public boolean deleteResult(int id) {

        return dao.deleteResult(id);

    }

    // SEARCH

    public Aptitude searchTopic(String topic) {

        return dao.searchTopic(topic);

    }

    // FILTER

    public List<Aptitude> filterStatus(String status) {

        return dao.filterStatus(status);

    }

    // REPORT

    public AptitudeReport getReport() {

        return dao.getReport();

    }

}