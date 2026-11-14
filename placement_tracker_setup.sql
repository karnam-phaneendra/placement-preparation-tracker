-- ==========================================================
-- PLACEMENT PREPARATION TRACKER - DATABASE SETUP SCRIPT
-- Database: placement_tracker
-- Course: Full Stack Java Development (APSCHE - ExcelR)
-- Student: Karnam Phaneendra
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `placement_tracker`;
USE `placement_tracker`;

-- Table 1: student
CREATE TABLE IF NOT EXISTS `student` (
  `student_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 2: company
CREATE TABLE IF NOT EXISTS `company` (
  `company_id` INT NOT NULL AUTO_INCREMENT,
  `company_name` VARCHAR(100) DEFAULT NULL,
  `role` VARCHAR(100) DEFAULT NULL,
  `application_date` DATE DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 3: aptitude
CREATE TABLE IF NOT EXISTS `aptitude` (
  `aptitude_id` INT NOT NULL AUTO_INCREMENT,
  `topic` VARCHAR(100) DEFAULT NULL,
  `score` INT DEFAULT NULL,
  `test_date` DATE DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`aptitude_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 4: dsa_progress
CREATE TABLE IF NOT EXISTS `dsa_progress` (
  `problem_id` INT NOT NULL AUTO_INCREMENT,
  `topic` VARCHAR(100) NOT NULL,
  `problem_name` VARCHAR(100) DEFAULT NULL,
  `platform` VARCHAR(50) DEFAULT NULL,
  `difficulty` VARCHAR(20) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT NULL,
  `date_solved` DATE DEFAULT NULL,
  PRIMARY KEY (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table 5: interview
CREATE TABLE IF NOT EXISTS `interview` (
  `interview_id` INT NOT NULL AUTO_INCREMENT,
  `company_name` VARCHAR(100) DEFAULT NULL,
  `round_name` VARCHAR(50) DEFAULT NULL,
  `interview_date` DATE DEFAULT NULL,
  `result` VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY (`interview_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Clean existing sample data to ensure fresh, consistent state
DELETE FROM `interview`;
DELETE FROM `dsa_progress`;
DELETE FROM `aptitude`;
DELETE FROM `company`;
DELETE FROM `student`;

-- Insert Student Account
INSERT INTO `student` (`student_id`, `name`, `email`, `password`) VALUES
(1, 'Karnam Phaneendra', 'kphani8937@gmail.com', 'Phani@123');

-- Insert Company Applications
INSERT INTO `company` (`company_id`, `company_name`, `role`, `application_date`, `status`) VALUES
(1, 'Infosys', 'System Engineer', '2026-07-10', 'Interview'),
(2, 'Tata Consultancy Services', 'Software Engineer', '2026-07-15', 'Shortlisted'),
(3, 'Wipro Technologies', 'Project Engineer', '2026-07-20', 'Applied'),
(4, 'Accenture', 'Associate Software Engineer', '2026-07-25', 'Selected'),
(5, 'Cognizant', 'Programmer Analyst', '2026-07-28', 'Rejected'),
(6, 'Capgemini', 'Senior Software Analyst', '2026-08-01', 'Applied');

-- Insert Aptitude Practice Records
INSERT INTO `aptitude` (`aptitude_id`, `topic`, `score`, `test_date`, `status`) VALUES
(1, 'Quantitative - Number Series', 88, '2026-07-19', 'Excellent'),
(2, 'Logical Reasoning - Syllogisms', 92, '2026-07-21', 'Excellent'),
(3, 'Verbal Ability - Reading Comprehension', 78, '2026-07-24', 'Good'),
(4, 'Data Interpretation - Bar Graphs', 65, '2026-07-26', 'Average'),
(5, 'Quantitative - Time and Work', 90, '2026-07-28', 'Excellent'),
(6, 'Logical - Blood Relations', 82, '2026-08-02', 'Good');

-- Insert DSA Problem Solving Records
INSERT INTO `dsa_progress` (`problem_id`, `topic`, `problem_name`, `platform`, `difficulty`, `status`, `date_solved`) VALUES
(1, 'Arrays', 'Two Sum', 'LeetCode', 'Easy', 'Solved', '2026-07-18'),
(2, 'Strings', 'Valid Palindrome', 'LeetCode', 'Easy', 'Solved', '2026-07-20'),
(3, 'Linked List', 'Reverse Linked List', 'HackerRank', 'Easy', 'Solved', '2026-07-22'),
(4, 'Trees', 'Binary Tree Inorder Traversal', 'LeetCode', 'Medium', 'Revision', '2026-07-24'),
(5, 'Dynamic Programming', 'Climbing Stairs', 'LeetCode', 'Medium', 'Solved', '2026-07-27'),
(6, 'Graphs', 'Number of Islands', 'LeetCode', 'Hard', 'Pending', '2026-07-30');

-- Insert Interview Schedules
INSERT INTO `interview` (`interview_id`, `company_name`, `round_name`, `interview_date`, `result`) VALUES
(1, 'Infosys', 'Technical Interview Round 1', '2026-08-05', 'Selected'),
(2, 'Tata Consultancy Services', 'Technical & HR Discussion', '2026-08-10', 'Pending'),
(3, 'Accenture', 'Final HR & Leadership Round', '2026-08-12', 'Selected'),
(4, 'Wipro Technologies', 'Coding Assessment Round', '2026-08-15', 'Pending'),
(5, 'Cognizant', 'Technical Round 1', '2026-07-29', 'Rejected');
