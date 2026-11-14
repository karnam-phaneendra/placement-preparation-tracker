package com.placementtracker.service;
import java.util.List;

import com.placementtracker.dao.DSAProgressDAO;
import com.placementtracker.model.DSAProgress;
import com.placementtracker.model.DSAReport;

public class DSAProgressService {

    private DSAProgressDAO dao = new DSAProgressDAO();

    public boolean addProblem(DSAProgress problem) {

        return dao.addProblem(problem);

    }
    public List<DSAProgress> getAllProblems() {

        return dao.getAllProblems();

    }
    public boolean updateProblemStatus(int problemId, String status) {

        return dao.updateProblemStatus(problemId, status);

    }
    public boolean deleteProblem(int problemId) {

        return dao.deleteProblem(problemId);

    }
    public DSAProgress searchProblem(String problemName) {

        return dao.searchProblem(problemName);

    }
    public List<DSAProgress> getProblemsByDifficulty(String difficulty) {

        return dao.getProblemsByDifficulty(difficulty);

    }
    public DSAReport getProgressReport() {

        return dao.getProgressReport();

    }

}