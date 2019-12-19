# Changelog

All notable changes to choerodon-front-agile will be documented in this file.

## [0.20.0] - 2019-12-20

### Add

- Remove strong association between use case and version
- By removing the hierarchical constraints of use case folders, users can create infinite use case folders, which enables users to more flexibly partition use cases.
- Plan and execution are combined to facilitate testers to manage test plans more intuitively.
- The new version of test plan supports automatic synchronization of use cases, which is convenient for users to quickly synchronize use cases to plan execution.
- Test plan supports updating content from use case to test plan according to own needs.
- Test plan adds a test overview to facilitate testers to quickly understand the test status of the plan
- Remove association between automated tests and use cases


## [0.16.0] - 2019-04-19

### Add

#### 0.16.0 Significantly added features

- Add test plan for loop or phase cloning can be bulk operation.

### Modify

#### 0.16.0 Significantly modify the feature

- Optimize test planning, test execution performance issues.
- Optimize the test experience, create test cycles, and optimize the time selector.
- Optimize the test experience. The test plan timeline can be dragged back and forth.
- Optimize the test experience, the edit phase of the test plan allows you to change the associated folder.
- Optimize the test experience, test defect report sort, from near to far according to creation time.
- Optimize the test experience. During test execution, click the number in the use case details to open a new window when entering the use case.
- Optimize the test experience, test reports, test cases in search number allowed with prefix.
### Fix

#### 0.16.0 Significant repair features

- Fix tree empty data error.
- Fix the test execution progress bar counting errors.


## [0.15.0] - 2019-03-22

### Add

#### 0.15.0 Significantly added features

- Add Filtering by label in `Test Case`
- Add related story features when creating defects in `Test Execution` details
- Add sort function for `Test Phase`in same `Test cycle`
- Add init demo data function

### Modify

#### 0.15.0 Significantly modify the feature

- Optimize the logic of update time in function clone `Test Cycle`

### Fix

#### 0.15.0 Significant repair features

- Fix the problem in `Test Execution` details when page turning
- Fix the problem in the `Test Plan` tree that is inconsistent with the detail progress bar on the right
- Fix the problem of database errors caused by excessively long reports in `Automation Tests`


## [0.14.0] - 2019-02-22

### Add

#### 0.14.0 Significantly added features

- Add `Test Results Report(color piece show)`.
- Add `Test Automation Framework` support- `TestNG`.

### Modify

#### 0.14.0 Significantly modify the feature

- Optimize `Execution Details` interface display.
- Optimize `Test Steps` cloning sorting operation.
- Optimize `Test Plan` page.
- Optimize `Test Plan` export function data sorting, operation.
- Optimize time display.
- Optimize `Custom Status` components of color card.
- Optimize `Test Cases` interface display Gantt chart - optimization `Test Plan` page edge rolling.
- Optimize `Tree` components according to the version.
- Optimize `Create Use Case` on version restrictions.
- Optimize `Test Execution` page.

### Fix

#### 0.14.0 Significant repair features

- Fix `Testing Phase` associated use case folder version shows error.
- Fix `Rich Text Edit Box` paste image repeating mistakes.
- Fix `Test Digest` page scrolling page form errors.
- Fix `Test Plan` page in the gantt chart change time produce a page fault.
- Fix `Create Bugs` is agent cannot search problem.
- Fix dragging in `Test Plans` modify specific date error problem.
- Fix `Test Defect Report` specific data show the wrong questions.


## [0.13.0] - 2019-01-11

### Modify

#### 0.13.0 Significantly modify the feature

- Optimize the defect correlation function in `Test Execution` .
- When importing test results, `Test Cycle` can auto scale its time range in `Automation Test` module.
- Automatically adapt the parent `Test Cycle` time when modifying the test phase time in `Test Plan` module.

### Fix

#### 0.13.0 Significant repair features

- Fix `Automated Test` does not update the status to a failed when an error occurs while running.
- Fix export error in `Test Case` when the version name contains spaces.
- Fix the problem that can not delete `Test Cycle` which created by `Automated Test`.


## [0.12.0] - 2018-12-14

### Add

#### 0.12.0 Significantly added features

- Add `Automation Test` module

### Modify

#### 0.12.0 Significantly modify the feature

- Add assign in batches function in the `Test Plan` moudle
- Show the priority and add filter with priority in `Test Plan` and `Test Execution` moudle

### Fix

#### 0.12.0 Significant repair features

- Fix the problem in export as excel moudle
- Fix the bug of data  in `Test Plan` and `Test Execution` when switching project
- Fix the bug of data in test step when switching the previous\next in the execution details

### Remove

#### 0.12.0 Significantly remove the feature

- Remove the fix data interface used in version 0.10.0


## [0.11.0] - 2018-11-16

### Add

#### 0.11.0 Significantly added features

- Test case can import through template excel file 
- Test case export function
- Test execution details page add before/next button

### Modify

#### 0.11.0 Significantly modify the feature

- Test execution export changed to asynchronous modification, adding progress bar
- Modify some interfaces for agile services
- Test case folder copying and moving support batch mode

### Fix

#### 0.11.0 Significant repair features

- the target of clone test phase in test plan modle support different version, cycle


## [0.10.0] - 2018-09-30

### Add

#### 0.10.0 Significantly added features

- Support oracle database
- Added unit test
- Added `test plan` function
- Added `folder` function in test case management 

### Modify

#### 0.10.0 Significantly modify the feature

- Modify the report data source selection operation, currently only display the data that have test association
- Modify how to add test exection through test case
- Test loop added user filtering function

### Fix

#### 0.10.0 Significant repair features

- The defect association table supplements the `project_id` field


## [0.9.0] - 2018-08-17

### Add

#### 0.9.0 Significantly added features

- Add multi-language interface,can be used for multi-language switching with the platform
- Add dashboard display interface
- The execution list adds a quick pass button, and if the test passes, it is not necessary to click on the details to adjust the execution state
- Increase the cycle export function, the user can export the contents of the cycle to excel
- Increase cycle cross-version cloning, users can copy test cycle to other versions for reuse
- Test cycle details table telescopic display, optimize the table content display after the tree diagram is collapsed
- Add some unit tests
- Add some api tests
- Added name verification when creating test cases
- The problem number increases the url, and the user does not have to switch to the agile interface to view the defect
- The cycle url is added to the execution record in the use case details, and the user can jump directly in the execution form in the use case details
- The default search of `use case management`, you do not need to select the field and then select it
- `Cycle Details` intetface increase according to personnel screening function,users can filter assignees or performers
- Support for redirect to new defect page when associating defects

### Modify

#### 0.9.0 Significantly modify the feature

- Optimized query interfaces such as reports, test cycle, test steps, and defects
- Event message changed to saga mode
- Test status icon style change
- `Test Summary` page interface integration optimization
- `Test Case Management` page to increase the display content
- `Test case management` remove extra sort fields
- Test execution can be edit in table
- Test strp in `Test Case Management` can be edit in table
- Optimize the `Report` page layout,column width does not change due to expansion

### Fix

#### 0.9.0 Significant repair features

- Fix `test cycle` and step pagination display problem
- Fix the problem that the count after deleting the test case will not be cascaded deleted
- Fix the problem that the page after execution is not automatically refreshed globally
- Fix execution details interface width compatibility error causes the editor button not to be seen
- Fixed a problem with pagination data error in `report`



## [0.8.0] - 2018-07-19

### Add

- Add test management function module
- Add test summary function
- Add test cycle function
- Add test case management function
- Add test report function
- Add test status management
- Users can manage their own test cases through the test management module
- Test cases can be hooked to agile modules