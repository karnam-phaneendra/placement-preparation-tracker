# PLACEMENT PREPARATION TRACKER — FUNCTIONAL TESTING REPORT

**Project Name:** Placement Preparation Tracker  
**Course:** Full Stack Java Development (APSCHE Internship - ExcelR)  
**Student Name:** Karnam Phaneendra (PIN: 24A85A6102)  
**Test Environment:** Windows 11 64-bit, JDK 1.8.0_503, MySQL Server 9.7, MySQL Connector/J 9.0.0  
**Database Schema:** `placement_tracker` on `localhost:3306`  
**Test Date:** November 10, 2026  

---

## 1. Executive Summary

This report documents the rigorous functional verification conducted on the **Placement Preparation Tracker** desktop application. Every functional requirement specified in the project guidelines—including database connectivity, authentication, CRUD operations across all four functional modules, analytical reporting, and data persistence across process restarts—was thoroughly tested and verified against the live MySQL database.

All twelve specified functional test cases executed successfully with a **100% pass rate** and zero fatal errors.

---

## 2. Test Execution Matrix

| Test ID | Module / Area | Test Input / Action | Expected Result | Actual Result | Status |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | **Application Startup** | Execute `java com.placementtracker.util.Main` | Application initializes with system look-and-feel and displays `LoginFrame` centered on screen. | `LoginFrame` displayed smoothly with brand header and input fields. | **PASSED** |
| **TC-02** | **Student Login (Valid)** | Enter Email: `kphani8937@gmail.com`<br/>Password: `Phani@123` | Application authenticates via `student` table, displays success message, and launches `MainFrame`. | Authenticated successfully; `MainFrame` opened with student profile "Karnam Phaneendra". | **PASSED** |
| **TC-03** | **Invalid Login Handling** | Enter Email: `wrong@gmail.com`<br/>Password: `invalid` | Access denied with red warning message: *"Invalid email or password. Access denied."* | Invalid login rejected; error banner shown; application remained secure. | **PASSED** |
| **TC-04** | **Dashboard Loading** | Automatic load on successful login or clicking *Dashboard* tab | Top welcome card, 4 KPI metric cards, recruitment pipeline, and readiness summary populated from MySQL. | Live KPIs displayed: 6 Companies, 6 Aptitude Tests, 4/6 DSA Solved, 5 Interviews. | **PASSED** |
| **TC-05** | **Adding a Company** | Name: `Capgemini`<br/>Role: `Senior Software Analyst`<br/>Date: `2026-08-01`<br/>Status: `Applied` | Record inserted into `company` table via `CompanyDAO.addCompany()`; table reloaded. | Record added to database; row populated in `JTable` with ID 6. | **PASSED** |
| **TC-06** | **Viewing Company Records** | Click *Company Management* in sidebar | All stored companies displayed in `JTable` with ID, Company, Role, Application Date, and Status. | All 6 company records rendered with clear column alignment and status badges. | **PASSED** |
| **TC-07** | **Updating & Deleting Company** | 1. Select row ID 1 (Infosys) & update status to `Interview`<br/>2. Select test row and confirm deletion | Status updated in MySQL; deleted record removed from table and database. | Status update reflected immediately; record deletion confirmed via dialog and removed from DB. | **PASSED** |
| **TC-08** | **Adding Aptitude Record** | Topic: `Logical - Blood Relations`<br/>Score: `82`<br/>Date: `2026-08-02`<br/>Status: `Good` | Record inserted into `aptitude` table; performance auto-classified; stat cards updated. | Test record inserted; `AptitudeReport` updated total tests to 6 and Good tests to 2. | **PASSED** |
| **TC-09** | **Recording DSA Progress** | Topic: `Graphs`<br/>Problem: `Number of Islands`<br/>Platform: `LeetCode`<br/>Difficulty: `Hard`<br/>Status: `Pending`<br/>Date: `2026-07-30` | Problem record saved into `dsa_progress`; completion percentage dynamically recalculated. | Record saved; `dsa_progress` updated; problem listed with LeetCode tag and Hard difficulty. | **PASSED** |
| **TC-10** | **Scheduling Interviews** | Company: `Cognizant`<br/>Round: `Technical Round 1`<br/>Date: `2026-07-29`<br/>Result: `Rejected` | Round persisted in `interview` table; clearance rates updated in Interview report. | Round scheduled; stored in MySQL; retrieved in live `JTable` with result *Rejected*. | **PASSED** |
| **TC-11** | **Viewing Progress Reports** | Click *Progress Reports* tab in sidebar | Four analytical cards computed: Placement conversion funnel, DSA completion (66.7%), Aptitude excellence (50.0%), Interview clearance (40.0%). | Dynamic analytics generated accurately; composite Placement Readiness Index displayed. | **PASSED** |
| **TC-12** | **Database Persistence & Restart** | 1. Terminate application process<br/>2. Relaunch application via `Main`<br/>3. Re-login as student | All newly inserted, updated, and deleted records persist unchanged in MySQL. | Verified complete data persistence; all 6 companies, 6 tests, 6 DSA, and 5 interviews retrieved intact. | **PASSED** |

---

## 3. Detailed Module Verification Findings

### 3.1 Student Login & Authentication
- **SQL Execution:** `SELECT * FROM student WHERE email=? AND password=?`
- **Security Check:** Verified that SQL injection attempts (e.g., `' OR '1'='1`) are completely neutralized due to JDBC parameterized `PreparedStatement`.
- **Session State:** Authenticated `Student` instance is passed to `MainFrame`, allowing dynamic display of the student's name (`Karnam Phaneendra`) and email across the interface and header bar.

### 3.2 Company Management Module
- **CRUD Operations:** All five core operations (*Create, Read, Update, Delete, Search*) function with instant visual synchronization.
- **Search & Sort:** Verified that `searchCompany()` performs substring matching against MySQL and that A-Z / Z-A sorting accurately orders the table by `company_name`.
- **Validation:** Missing company name or invalid date format triggers descriptive error dialogs without application crashes.

### 3.3 Aptitude Tracking Module
- **Automated Grading:** When typing numeric scores into the score field, a `DocumentListener` automatically suggests the corresponding performance category:
  - $\ge 85$: *Excellent*
  - $70 - 84$: *Good*
  - $50 - 69$: *Average*
  - $< 50$: *Needs Improvement*
- **Database Alignment:** All records properly store SQL `Date` objects and integer scores.

### 3.4 Data Structures & Algorithms (DSA) Progress Tracker
- **Multi-Platform Support:** Tested dropdown selection across LeetCode, HackerRank, GeeksforGeeks, CodeChef, and Codeforces.
- **Difficulty & Status Filtering:** Verified filtering problems by difficulty (*Easy, Medium, Hard*) and resolution status (*Solved, Revision, Pending*).

### 3.5 Interview Scheduling Module
- **Stage Tracking:** Verified tracking of diverse campus recruitment rounds (*Online Assessment, Technical Round 1, Technical Round 2, HR Discussion*).
- **Date Validation:** Enforces standard `YYYY-MM-DD` date formatting for SQL compatibility.

### 3.6 Reports & Analytical Intelligence
- **Aggregation Logic:** Verified SQL aggregation queries (`COUNT(*)`, `SUM(CASE WHEN ... THEN 1 ELSE 0 END)`) computing accurate conversion rates and completion metrics.
- **Dynamic Recomputation:** The *↻ Recompute All Reports* button immediately recalculates statistics whenever new records are added or updated.

### 3.7 Database Diagnostics & Operations Console
- **Connection Verification:** Confirmed active metadata reporting from `com.mysql.cj.jdbc.Driver` on MySQL 9.7 with latency of under 5 ms.
- **Table Integrity:** Live counting verified:
  - `student`: 1 record
  - `company`: 6 records
  - `aptitude`: 6 records
  - `dsa_progress`: 6 records
  - `interview`: 5 records

---

## 4. Current Limitations & Scope for Enhancement

1. **Local MySQL Dependency:** The application currently relies on a locally hosted MySQL instance on `localhost:3306`. Future iterations can configure cloud database connectivity (e.g., AWS RDS or Railway MySQL).
2. **Desktop Platform:** The user interface is currently implemented as a Java Swing desktop application. Future roadmaps include migrating the frontend to React.js with a Spring Boot REST API.
3. **Automated Reminders:** Interview notifications currently require opening the scheduler; future enhancements could integrate desktop push notifications or automated email/SMS reminders via Twilio or JavaMail.

---

## 5. Conclusion

The functional verification confirms that the **Placement Preparation Tracker** is fully operational, thoroughly tested, and completely fulfills all curriculum objectives set forth by the APSCHE Full Stack Java Development program.
